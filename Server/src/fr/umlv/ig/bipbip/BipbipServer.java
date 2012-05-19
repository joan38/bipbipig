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
package fr.umlv.ig.bipbip;

import fr.umlv.ig.bipbip.poi.defined.FixedSpeedCam;
import fr.umlv.ig.bipbip.poi.defined.MobileSpeedCam;
import fr.umlv.ig.bipbip.poi.defined.RoadWorks;
import fr.umlv.ig.bipbip.server.ClientCommandHandler;
import fr.umlv.ig.bipbip.server.ServerPOIList;
import fr.umlv.ig.bipbip.server.gui.ServerJFrame;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
    private final ServerSocketChannel ssc;
    // POI.
    /**
     * List of POI.
     *
     * Threadsafe :)
     */
    private ServerPOIList poiList = new ServerPOIList();

    /**
     * Instantiates a bipbip server.
     *
     * @param port
     * @throws IOException
     */
    public BipbipServer(int port) throws IOException {
        ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port), MAX_CONNECTIONS);
    }

    /**
     * Launch the server.
     */
    public void serve() {
        System.out.println("Starting server");
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            new Thread() {

                @Override
                public void run() {
                    for (;;) {
                        synchronized (ssc) {
                            SocketChannel sc;
                            try {
                                System.out.println("Accept");
                                sc = ssc.accept();
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
            }.start();
        }
    }

    /**
     * Handles a client.
     *
     * @param sc SocketChannel with the client.
     */
    private void serveClient(final SocketChannel sc) {
        System.out.println("Dealing with client...");
        Scanner scanner = new Scanner(sc, NetUtils.getCharset().name());
        try {
            while (scanner.hasNextLine()) {
                System.out.println("lecture depuis le réseau");
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
            System.out.println("...end of client connection");
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
    public ServerPOIList getPoiList() {
        return poiList;
    }

    /**
     * Launch bipbip server.
     *
     * @param args Unused.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Please give the listening port in arguments: <port>");
            return;
        }
        
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }

        // Setting up loggers.
        Logger clientCommandLogger = Logger.getLogger("fr.umlv.ig.bipbip.server.ClientCommandHandler");

        //clientCommandLogger.addHandler(new ConsoleHandler()); // Default handler.

        Logger logger = Logger.getLogger("fr.umlv.ig.bipbip.server.ServerPOIList");
        logger.setLevel(Level.ALL);
        logger.addHandler(new ConsoleHandler());

        /*
         * Our protocol requires that we work with the US locale for both
         * doubles and dates
         */
        Locale.setDefault(Locale.US);
        BipbipServer server = new BipbipServer(Integer.parseInt(args[0]));
        server.serve();
        
        // Some dummy points.
        server.getPoiList().addPOI(new FixedSpeedCam(50, 50));
        server.getPoiList().addPOI(new MobileSpeedCam(100, 100));
        server.getPoiList().addPOI(new RoadWorks(2.709975, 48.836659));

        // Launching the server GUI.
        ServerJFrame frame = new ServerJFrame(server, clientCommandLogger, server.getPoiList());
        frame.setSize(500, 500);
        frame.setVisible(true);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit")) {
                scanner.close();
                System.exit(0);
            }
            System.err.println("Unknown command: " + line);
        }
    }
}