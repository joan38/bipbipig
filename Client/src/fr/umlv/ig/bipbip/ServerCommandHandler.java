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

import fr.umlv.ig.bipbip.Event;
import fr.umlv.ig.bipbip.EventType;
import fr.umlv.ig.bipbip.NetUtil;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles a server command.
 * 
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public enum ServerCommandHandler {
    
    INFOS {
        /**
         * A INFOS command is supposed to have the following form:
         * 
         * INFOS N
         * line_1
         * ...
         * line_N
         * 
         * where N is the number of lines of information. Each line is of
         * the form:
         * 
         * INFO EVENT_TYPE X Y
         */
        @Override
        public ArrayList<Event> handle(SocketChannel sc, Scanner scanner) throws IOException {
            if (!scanner.hasNextLine()) throw new IOException("Invalid command");
            String line=scanner.nextLine();
            int n;
            try {
                while (line.startsWith(" ")) {
                    line=line.substring(1);
                }
                n=Integer.parseInt(line);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid integer value: "+line);
            }
            ArrayList<Event> list=new ArrayList<Event>();
            for (int i=0;i<n;i++) {
                if (!scanner.hasNext() || !scanner.next().equals(ServerCommandHandler.INFO.name())) {
                    throw new IOException("Missing INFO answer");
                }
                list.add((Event) ServerCommandHandler.INFO.handle(sc,scanner));
            }
            return list;
        }
                
    },
    
    INFO {

        /**
         * A INFO command is supposed to have the following form:
         * 
         * INFO EVENT_TYPE X Y
         * 
         * where X and Y are double
         */
        @Override
        public Event handle(SocketChannel sc, Scanner scanner) throws IOException {
            if (!scanner.hasNext()) throw new IOException("Invalid command");
            EventType event;
            double x,y;
            try {
                event=EventType.valueOf(scanner.next());
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid event type");
            }
            String tmp=scanner.next();
            try {
                x=Double.parseDouble(tmp);
            } catch (NumberFormatException e) {
                throw new IOException("Missing X coordinate");
            }
            tmp=scanner.nextLine();
            while (tmp.startsWith(" ")) {
                tmp=tmp.substring(1);
            }
            try {
                y=Double.parseDouble(tmp);
            } catch (NumberFormatException e) {
                throw new IOException("Missing Y coordinate");
            }
            return new Event(event,x,y);
        }
        
    };
    
    public abstract Object handle(SocketChannel sc,Scanner scanner) throws IOException;
}
