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
package fr.umlv.ig.bipbip.poi.defined;

import fr.umlv.ig.bipbip.EventType;
import fr.umlv.ig.bipbip.poi.SimplePOI;
import java.util.Date;

/**
 * Represent all unknown POI.
 * 
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class Divers extends SimplePOI {

    public Divers(double positionX, double positionY) {
        super(positionX, positionY, EventType.DIVERS);
    }

    public Divers(double positionX, double positionY, Date date) {
        super(positionX, positionY, EventType.DIVERS, date);
    }
}
