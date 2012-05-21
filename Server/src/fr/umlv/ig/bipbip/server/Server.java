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

import fr.umlv.ig.bipbip.server.data.ServerPoiList;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bipbip server class.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class Server {

    // Server logger.
    public static final Logger logger = Logger.getLogger(Server.class.getName());
    // Network related things.
    private final static int MAX_CONNECTIONS = 32;
    private ServerSocketChannel ssc;
    public static final int DEFAULT_PORT = 6996;
    // POI.
    private ServerPoiList poiList = new ServerPoiList();
    private final int port;
    // List of current communications.
    private final Map<ServerCommunication, Thread> communications = new HashMap<ServerCommunication, Thread>();

    /**
     * Instantiates a server.
     *
     * @param port Port to bind to.
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Gets the server port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns if the server is running or not.
     *
     * @return The server running state.
     */
    public boolean getConnected() {
        return ssc.isOpen() && ssc.socket().isBound();
    }

    /**
     * Disconnect the server.
     */
    public void disconnect() {
        logger.info("Stopping server...");
        try {
            ssc.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage());
        }
        for (Map.Entry<ServerCommunication, Thread> entry : communications.entrySet()) {
            entry.getKey().shutdown();
        }
        logger.info("Server stopped");
    }

    /**
     * Launch the server.
     */
    public void serve() {
        logger.log(Level.INFO, "Starting the server on port "+ port);
        try {
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(this.port), MAX_CONNECTIONS);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage());
            return;
        }

        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            ServerCommunication communication = new ServerCommunication(ssc, poiList);
            Thread t = new Thread(communication);
            communications.put(communication, t);
            t.start();
        }

        logger.log(Level.INFO, "Server started.");
    }

    /**
     * Gets the list of POI.
     *
     * @return The list of POI.
     */
    public ServerPoiList getPoiList() {
        return poiList;
    }
}
