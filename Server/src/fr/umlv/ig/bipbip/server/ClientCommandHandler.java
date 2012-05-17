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

import fr.umlv.ig.bipbip.Event;
import fr.umlv.ig.bipbip.EventType;
import fr.umlv.ig.bipbip.NetUtil;
import fr.umlv.ig.bipbip.poi.POI;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles a client command.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public enum ClientCommandHandler {

    SUBMIT {

        /**
         * A SUBMIT command is supposed to have the following form:
         *
         * SUBMIT EVENT X Y DATE
         *
         * where X and Y are double, and DATE is a full Date in the US locale.
         *
         * SUBMIT is used by a client that want to report the existence of
         * something
         */
        @Override
        public void handle(SocketChannel sc, Scanner scanner, ServerPOIList poiList) throws IOException {
            if (!scanner.hasNext()) {
                throw new IOException("Invalid command");
            }
            EventType event;
            double x, y;
            Date date;
            try {
                event = EventType.valueOf(scanner.next());
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid event type");
            }
            if (!scanner.hasNextDouble()) {
                throw new IOException("Missing X coordinate");
            }
            x = scanner.nextDouble();
            if (!scanner.hasNextDouble()) {
                throw new IOException("Missing Y coordinate");
            }
            y = scanner.nextDouble();
            if (!scanner.hasNext()) {
                throw new IOException("Missing time coordinate");
            }
            String d = scanner.nextLine();
            try {
                /*
                 * We have to remove leading spaces, because they will disturb
                 * DateFormat.parse()
                 */
                while (d.startsWith(" ")) {
                    d = d.substring(1);
                }
                date = NetUtil.getDateformat().parse(d);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Invalid date: " + d);
            }
            /*
             * Handling commands
             */
            // Creation of the POI.
            POI poi = event.constructPOI(x, y, date);

            // Adding the POI to the collection.
            poiList.addPOI(poi);

            logger.log(Level.INFO, "CLIENT: SUBMIT " + event.name() + " " + x + " " + y + " " + NetUtil.getDateformat().format(date));
        }
    },
    NOT_SEEN {

        /**
         * A NOT_SEEN command is supposed to have the following form:
         *
         * NOT_SEEN EVENT X Y DATE
         *
         * where X and Y are double, and DATE is a full Date in the US locale
         *
         * NOT_SEEN is used by a client that want to report that he/she didn't
         * see an event reported by the server
         */
        @Override
        public void handle(SocketChannel sc, Scanner scanner, ServerPOIList poiList) throws IOException {
            if (!scanner.hasNext()) {
                throw new IOException("Invalid command");
            }
            EventType event;
            double x, y;
            Date date;
            try {
                event = EventType.valueOf(scanner.next());
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid event type");
            }
            if (!scanner.hasNextDouble()) {
                throw new IOException("Missing X coordinate");
            }
            x = scanner.nextDouble();
            if (!scanner.hasNextDouble()) {
                throw new IOException("Missing Y coordinate");
            }
            y = scanner.nextDouble();
            if (!scanner.hasNext()) {
                throw new IOException("Missing time coordinate");
            }
            String d = scanner.nextLine();
            try {
                /*
                 * We have to remove leading spaces, because they will disturb
                 * DateFormat.parse()
                 */
                while (d.startsWith(" ")) {
                    d = d.substring(1);
                }
                date = NetUtil.getDateformat().parse(d);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Invalid date: " + d);
            }
            // Looking for the POI.
            ArrayList<POI> poiAt = poiList.getPOIAt(x, y, event);

            // Looking for the POI dates.
            POI poi = null;
            for (POI p : poiAt) {
                if (p.getDate().equals(date)) {
                    poi = p;
                    break;
                }
            }

            // Woot, poi found.
            if (poi != null) {
                poiList.notSeen(poi);
            }

            logger.log((poi == null) ? Level.WARNING : Level.INFO, "CLIENT: NOT_SEEN " + event.name() + " " + x + " " + y + " " + NetUtil.getDateformat().format(date));
        }
    },
    GET_INFO {

        /**
         * A GET_INFO command is supposed to have the following form:
         *
         * GET_INFO X Y
         *
         * where X and Y are double
         */
        @Override
        public void handle(SocketChannel sc, Scanner scanner, ServerPOIList poiList) throws IOException {
            double x, y;
            if (!scanner.hasNextDouble()) {
                throw new IOException("Missing X coordinate");
            }
            x = scanner.nextDouble();
            if (!scanner.hasNextDouble()) {
                throw new IOException("Missing Y coordinate");
            }
            y = scanner.nextDouble();
            /*
             * Retrieving what the client requested.
             */
            logger.log(Level.INFO, "CLIENT: GET_INFO " + x + " " + y);

            // Getting the points 40km square.
            SortedSet<POI> points = poiList.getPointsBetween(x - squareArea, y - squareArea, x + squareArea, y + squareArea);

            // Creation of the arraylist.
            ArrayList<Event> list = new ArrayList<Event>(points.size());
            for (POI p : points) {
                list.add(p.toEvent());
            }

            // Sending the answer.
            ServerCommand.sendInfos(sc, list);
        }
    };
    /**
     * 20km square area.
     *
     * Area of event that will be sent to the client.
     */
    private static final double squareArea = 0.247;
    // Logger
    private static final Logger logger = Logger.getLogger("fr.umlv.ig.bipbip.server.ClientCommandHandler");

    public abstract void handle(SocketChannel sc, Scanner scanner, ServerPOIList poiList) throws IOException;
}
