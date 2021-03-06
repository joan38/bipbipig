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

import fr.umlv.ig.bipbip.poi.Poi;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Commands available for the server.
 * 
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class ServerCommand {

    public static void sendInfos(SocketChannel sc, ArrayList<Poi> list) throws IOException {
        NetUtils.writeLine(sc, "INFOS " + list.size());
        for (Poi e : list) {
            sendEventInfo(sc, e);
        }
    }

    private static void sendEventInfo(SocketChannel sc, Poi e) throws IOException {
        NetUtils.writeLine(sc, "INFO " + e.getType().name() + " " + e.getLat() + " " + e.getLon() + " " + NetUtils.getDateformat().format(e.getDate()) + " " + e.getConfirmations());
    }
}
