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

import fr.umlv.ig.bipbip.server.communication.Server;
import fr.umlv.ig.bipbip.server.communication.ServerCommunication;
import fr.umlv.ig.bipbip.server.gui.ServerJFrame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BipBip server.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class BipbipServer {

    /**
     * Launch bipbip server.
     *
     * @param args Specified port to use. If undefined the default port is used.
     */
    public static void main(String[] args) throws IOException {
        int port = Server.DEFAULT_PORT;

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
        //
        Logger clientCommandLogger = Logger.getLogger("fr.umlv.ig.bipbip.server.ClientCommandHandler");
        loggersToDisplayInGui.add(clientCommandLogger);
        //clientCommandLogger.addHandler(new ConsoleHandler()); // Default handler.

        // Server and communication loggers.
        Server.logger.setLevel(Level.ALL);
        loggersToDisplayInGui.add(Server.logger);
        
        ServerCommunication.logger.setLevel(Level.ALL);
        loggersToDisplayInGui.add(ServerCommunication.logger);

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
        Locale.setDefault(Locale.ROOT);
        Server server = new Server(port);

        // Launching the server GUI.
        ServerJFrame frame = new ServerJFrame(server, loggersToDisplayInGui, server.getPoiList());
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
