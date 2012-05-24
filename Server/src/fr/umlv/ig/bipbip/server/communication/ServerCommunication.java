/*
 * Copyright (C) 2012 Damien Girard <dgirard@nativesoft.fr>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.umlv.ig.bipbip.server.communication;

import fr.umlv.ig.bipbip.server.data.ServerPoiList;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Communications between the server and a client.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class ServerCommunication implements Runnable {

    public static final Logger logger = Logger.getLogger(ServerCommunication.class.getName());
    private final ServerSocketChannel ssc;
    private SocketChannel sc;
    private Scanner scanner;
    private final ServerPoiList poiList;
    private final AtomicBoolean requestShutdown = new AtomicBoolean(false);

    /**
     * Instantiate a server/client communication.
     *
     * @param ssc Server socket channel to use.
     * @param poiList Database holdings the points of interests.
     *
     * @see #run()
     */
    public ServerCommunication(ServerSocketChannel ssc, ServerPoiList poiList) {
        this.ssc = ssc;
        this.poiList = poiList;
    }

    /**
     * Shutdown properly the communication.
     */
    public void shutdown() {
        requestShutdown.set(true);
        if (sc != null) {
            try {
                sc.close(); // Closing the socket.
            } catch (IOException ignored) {
            }
        }
        if (scanner != null) {
            try {
                scanner.close();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    @Override
    public void run() {
        while (requestShutdown.get() == false) {
            synchronized (ssc) {
                try {
                    sc = ssc.accept();
                    logger.log(Level.INFO, "Accept " + sc.getRemoteAddress().toString());
                } catch (ClosedChannelException e) {
                    return; // Terminating the thread. Server is closed.
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Accept " + e.getLocalizedMessage());
                    continue;
                }
            }
            
            try {
                serveClient(sc);
            } finally {
                try {
                    sc.close();
                } catch (IOException ignored) {
                    continue;
                }
            }
        }
    }

    /**
     * Handles a client.
     *
     * @param sc SocketChannel with the client.
     */
    private void serveClient(final SocketChannel sc) {
        logger.fine("Dealing with client...");
        scanner = new Scanner(sc);
        try {
            while (requestShutdown.get() == false && scanner.hasNextLine()) {
                logger.log(Level.FINE, "Lecture depuis le réseau. " + sc.getRemoteAddress().toString());
                String line = scanner.nextLine();
                Scanner tmp_scanner = new Scanner(line);
                if (!tmp_scanner.hasNext()) {
                    break;
                }
                String foo = tmp_scanner.next();
                try {
                    ClientCommandHandler cmd = ClientCommandHandler.valueOf(foo);
                    cmd.handle(sc, tmp_scanner, poiList);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Invalid command: " + line);
                }
            }
        } catch (IOException ie) {
            if (requestShutdown.get() != true) { // Ignoring the error message if shutdown is requested.
                logger.log(Level.SEVERE, ie.getLocalizedMessage());
            }
        } finally {
            logger.info("...end of client connection");
            try {
                scanner.close();
                sc.close();
            } catch (IOException ignored) {
            }
        }
    }
}
