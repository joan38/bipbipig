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

import fr.umlv.ig.bipbip.poi.Poi;
import fr.umlv.ig.bipbip.poi.PoiType;
import fr.umlv.ig.bipbip.server.data.ServerPoiList;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
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
         * SUBMIT EVENT X Y DATE NB_CONFIRMATION
         *
         * where X and Y are double, and DATE is a full Date in the US locale.
         *
         * SUBMIT is used by a client that want to report the existence of
         * something
         */
        @Override
        public void handle(SocketChannel sc, Scanner scanner, ServerPoiList poiList) throws IOException {
            if (!scanner.hasNext()) {
                throw new IOException("Invalid command");
            }
            PoiType event;
            double x, y;
            Date date;
            try {
                event = PoiType.valueOf(scanner.next());
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
            String d = scanner.next();
            try {
                /*
                 * We have to remove leading spaces, because they will disturb
                 * DateFormat.parse()
                 */
                while (d.startsWith(" ")) {
                    d = d.substring(1);
                }
                date = NetUtils.getDateformat().parse(d);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Invalid date: " + d);
            }
            int confirmations = scanner.nextInt();
            /*
             * Handling commands
             */
            // Creation of the POI.
            Poi poi = event.constructPoi(x, y, date, confirmations);

            // Adding the POI to the collection.
            poiList.addPoi(poi);

            logger.log(Level.INFO, "CLIENT: SUBMIT " + event.name() + " " + x + " " + y + " " + NetUtils.getDateformat().format(date) + " " + confirmations);
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
        public void handle(SocketChannel sc, Scanner scanner, ServerPoiList poiList) throws IOException {
            if (!scanner.hasNext()) {
                throw new IOException("Invalid command");
            }
            PoiType event;
            double x, y;
            Date date;
            try {
                event = PoiType.valueOf(scanner.next());
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
                date = NetUtils.getDateformat().parse(d);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Invalid date: " + d);
            }
            // Looking for the POI.
            ArrayList<Poi> poiAt = poiList.getPoiAt(x, y, event);

            // Looking for the POI dates.
            Poi poi = null;
            for (Poi p : poiAt) {
                if (p.getDate().equals(date)) {
                    poi = p;
                    break;
                }
            }

            // Woot, poi found.
            if (poi != null) {
                poiList.notSeen(poi);
            }

            logger.log((poi == null) ? Level.WARNING : Level.INFO, "CLIENT: NOT_SEEN " + event.name() + " " + x + " " + y + " " + NetUtils.getDateformat().format(date));
        }
    },
    GET_INFOS {

        /**
         * A GET_INFOS command is supposed to have the following form:
         *
         * GET_INFOS X Y
         *
         * where X and Y are double
         */
        @Override
        public void handle(SocketChannel sc, Scanner scanner, ServerPoiList poiList) throws IOException {
            double latitude, longitude;
            if (!scanner.hasNextDouble()) {
                throw new IOException("Missing X coordinate");
            }
            latitude = scanner.nextDouble();
            if (!scanner.hasNextDouble()) {
                throw new IOException("Missing Y coordinate");
            }
            longitude = scanner.nextDouble();
            /*
             * Retrieving what the client requested.
             */
            logger.log(Level.INFO, "CLIENT: GET_INFOS " + latitude + " " + longitude);

            // Getting the points 40km square.
            ArrayList<Poi> points = poiList.getPointsInAreaBetween(latitude, longitude, SQUARE_AREA);

            // Sending the answer.
            ServerCommand.sendInfos(sc, points);
        }
    };
    /**
     * 20km square area.
     *
     * Area of event that will be sent to the client.
     */
    private static final double SQUARE_AREA = 20000;
    // Logger
    private static final Logger logger = Logger.getLogger("fr.umlv.ig.bipbip.server.ClientCommandHandler");

    public abstract void handle(SocketChannel sc, Scanner scanner, ServerPoiList poiList) throws IOException;
}
