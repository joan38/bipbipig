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
package fr.umlv.ig.bipbip.server;

import fr.umlv.ig.bipbip.poi.defined.FixedSpeedCam;
import fr.umlv.ig.bipbip.poi.defined.MobileSpeedCam;
import fr.umlv.ig.bipbip.poi.defined.RoadWorks;
import fr.umlv.ig.bipbip.server.gui.ServerJFrame;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * BipBip server.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class BipbipServer {

    // Network related things.
    private final static int MAX_CONNECTIONS = 32;
    private ServerSocketChannel ssc;
    private static final int DEFAULT_PORT = 6996;
    // Server logger.
    private static final Logger logger = Logger.getLogger("fr.umlv.ig.bipbip.server.BipbipServer");
    // POI.
    /**
     * List of POI.
     *
     * Threadsafe :)
     */
    private ServerPoiList poiList = new ServerPoiList();
    private final int serverPort;

    /**
     * Instantiates a bipbip server.
     *
     * @param port
     * @throws IOException
     */
    public BipbipServer(int port) throws IOException {
        this.serverPort = port;
    }

    /**
     * Gets the server port.
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Returns if the server is running or not.
     *
     * @return The server running state.
     */
    public boolean getConnected() {
        return ssc.isOpen();
    }

    /**
     * Disconnect the server.
     */
    public void disconnect() {
        /*
         * for (Thread thread : threadList) { thread.interrupt(); try {
         * thread.join(); } catch (InterruptedException ex) {
         * Logger.getLogger(BipbipServer.class.getName()).log(Level.SEVERE,
         * null, ex); } }
         */

        logger.info("Stopping server...");
        try {
            ssc.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        logger.info("Server stopped");
    }
    private final ArrayList<Thread> threadList = new ArrayList<Thread>();

    /**
     * Launch the server.
     */
    public void serve() {
        logger.info("Starting server...");
        try {
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(this.serverPort), MAX_CONNECTIONS);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    for (;;) {
                        synchronized (ssc) {
                            SocketChannel sc;
                            try {
                                sc = ssc.accept();
                                logger.info("Accept");
                            } catch (ClosedChannelException e) {
                                return; // Terminating the thread. Server is closed.
                            } catch (IOException e) {
                                e.printStackTrace();
                                continue;
                            }
                            
                            try {
                                serveClient(sc);
                            } finally {
                                try {
                                    sc.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    continue;
                                }
                            }
                        }

                    }
                }
            };
            threadList.add(t);
            t.start();
        }
        logger.info("Server started.");
    }

    /**
     * Handles a client.
     *
     * @param sc SocketChannel with the client.
     */
    private void serveClient(final SocketChannel sc) {
        logger.info("Dealing with client...");
        Scanner scanner = new Scanner(sc);
        try {
            while (scanner.hasNextLine()) {
                logger.fine("lecture depuis le réseau");
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
            ie.printStackTrace();
        } finally {
            logger.info("...end of client connection");
            try {
                scanner.close();
                sc.close();
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }
    }

    /**
     * Gets the list of POI.
     *
     * @return The list of POI.
     */
    public ServerPoiList getPoiList() {
        return poiList;
    }

    /**
     * Launch bipbip server.
     *
     * @param args Specified port to use. If undefined the default port is used.
     */
    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;

        System.out.println("Bipbip Server. By Damien Girard and Joan Goyeau.");
        System.out.println("Usage: BipbipServer <port>");

        // If no port, using the default one.
        if (args.length < 1) {
            System.out.println("No port specified. Using default.");
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                System.err.println("Cannot read the port to use. " + exception.getLocalizedMessage());
                return;
            }
        }

        ArrayList<Logger> loggersToDisplayInGui = new ArrayList<Logger>();

        // Setting up loggers.
        Logger clientCommandLogger = Logger.getLogger("fr.umlv.ig.bipbip.server.ClientCommandHandler");
        loggersToDisplayInGui.add(clientCommandLogger);
        //clientCommandLogger.addHandler(new ConsoleHandler()); // Default handler.

        // Server communication logger.
        logger.setLevel(Level.ALL);
        loggersToDisplayInGui.add(logger);

        // Poi operations Debug logger.
        Logger poiLogger = Logger.getLogger("fr.umlv.ig.bipbip.server.ServerPoiList");
        poiLogger.setLevel(Level.ALL);
        poiLogger.addHandler(new ConsoleHandler());
        // Debug.
        loggersToDisplayInGui.add(poiLogger);

        /*
         * Our protocol requires that we work with the US locale for both
         * doubles and dates
         */
        Locale.setDefault(Locale.US);
        BipbipServer server = new BipbipServer(port);

        // Some dummy points.
        server.getPoiList().addPOI(new FixedSpeedCam(50, 50, new Date()));
        server.getPoiList().addPOI(new MobileSpeedCam(100, 100, new Date()));
        server.getPoiList().addPOI(new RoadWorks(48.836659, 2.709975, new Date()));

        // Launching the server GUI.
        ServerJFrame frame = new ServerJFrame(server, loggersToDisplayInGui, server.getPoiList());
        frame.setSize(800, 500);
        frame.setVisible(true);

        // Launching the server.
        server.serve();

        /*
         * Scanner scanner = new Scanner(System.in); while
         * (scanner.hasNextLine()) { String line = scanner.nextLine(); if
         * (line.equalsIgnoreCase("exit")) { scanner.close(); System.exit(0); }
         * System.err.println("Unknown command: " + line); }
         */
    }
}
