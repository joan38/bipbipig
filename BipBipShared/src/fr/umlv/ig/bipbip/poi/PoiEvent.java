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
package fr.umlv.ig.bipbip.poi;

/**
 * Point of Interest event.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiEvent {

    private final Object source;
    private final Poi poi;
    private final Exception exception;

    public PoiEvent(Object source, Poi poi, Exception exception) {
        this.poi = poi;
        this.source = source;
        this.exception = exception;
    }
    
    /**
     * Creates a new point of interest event.
     *
     * @param source Object that emit the event.
     * @param poi Object emitting the event.
     */
    public PoiEvent(Object source, Poi poi) {
        this(source, poi, null);
    }

    /**
     * Gets the point of interest of the event.
     *
     * @return The POI.
     */
    public Poi getPoi() {
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

    /**
     * Gets the exception of the event if any error was thrown.
     * 
     * @return The exception thrown.
     */
    public Exception getException() {
        return exception;
    }
}
