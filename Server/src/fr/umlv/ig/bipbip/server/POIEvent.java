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

import fr.umlv.ig.bipbip.poi.POI;

/**
 * Point of Interest event.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class POIEvent {

    private final POI poi;
    private final Object source;

    /**
     * Creates a new point of interest event.
     *
     * @param source Object that emit the event.
     * @param poi Object emitting the event.
     */
    public POIEvent(Object source, POI poi) {
        this.poi = poi;
        this.source = source;
    }

    /**
     * Gets the point of interest of the event.
     *
     * @return The POI.
     */
    public POI getPoi() {
        return poi;
    }

    /**
     * Gets the emitter of the event.
     *
     * @return The object that emitted the event.
     */
    public Object getSource() {
        return source;
    }
}
