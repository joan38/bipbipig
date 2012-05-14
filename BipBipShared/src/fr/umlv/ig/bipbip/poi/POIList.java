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

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Generic collection of point of interests.
 *
 * The implementation rely on a synchronized sorted map. (No concurrency
 * problem).
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class POIList {

    // TODO: Hum, pas bon l'idée de la SortedMap, trouver peut être une idée 
    //       à base de matrice.
    private final SortedMap<Double, ArrayList<POI>> points;
    private final ConcurrentLinkedDeque<POIListener> listeners;

    /**
     * Create an empty list of POI.
     */
    public POIList() {
        points = Collections.synchronizedSortedMap(new TreeMap<Double, SortedMap<Double, POI>>());
        listeners = new ConcurrentLinkedDeque<POIListener>();
    }

    /**
     * Adds a POI listener to the collection.
     *
     * @param listener The POIListener to be added.
     */
    public void addPOIListener(POIListener listener) {
        Objects.requireNonNull(listener);

        listeners.add(listener);
    }

    /**
     * Removes a POI listener from the collection.
     *
     * @param listener The POIListener to be removed.
     */
    public void removePOIListener(POIListener listener) {
        Objects.requireNonNull(listener);

        listeners.remove(listener);
    }

    /**
     * Fires the listeners when a POI is added.
     *
     * @param e Event.
     */
    protected void firePOIAdded(POIEvent e) {
        for (POIListener listener : listeners) {
            listener.poiAdded(e);
        }
    }

    /**
     * Fires the listeners when a POI is removed.
     *
     * @param e Event.
     */
    protected void firePOIRemoved(POIEvent e) {
        for (POIListener listener : listeners) {
            listener.poiRemoved(e);
        }
    }

    public void addPOI(POI p) {
        // Is there is any collisions?
        // Very low chance to have one, but maybe.
        ArrayList list = points.get(p.getY());
        if (list != null) {
            // Doh, a collision. Well this can happen sometimes even in the double world.
            list.add(p);
        } else {
            points.put(p., list)
        }
    }

    public void removePOI(POI p) {
        // TODO add implementation
    }
}
