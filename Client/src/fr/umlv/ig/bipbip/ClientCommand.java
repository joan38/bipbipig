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

import fr.umlv.ig.bipbip.EventType;
import fr.umlv.ig.bipbip.NetUtil;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Date;

/**
 * Commands available for the client.
 * 
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class ClientCommand {

    public static void submit(SocketChannel sc, EventType event, double x, double y, Date date) throws IOException {
        NetUtil.writeLine(sc, "SUBMIT " + event.name() + " " + x + " " + y + " " + NetUtil.getDateformat().format(date));
    }

    public static void notSeen(SocketChannel sc, EventType event, double x, double y, Date date) throws IOException {
        NetUtil.writeLine(sc, "NOT_SEEN " + event.name() + " " + x + " " + y + " " + NetUtil.getDateformat().format(date));
    }

    public static void getInfo(SocketChannel sc, double x, double y) throws IOException {
        NetUtil.writeLine(sc, "GET_INFO " + x + " " + y);
    }
}
