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

import fr.umlv.ig.bipbip.EventType;
import fr.umlv.ig.bipbip.poi.POI;
import fr.umlv.ig.bipbip.poi.SimplePOI;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Generic collection of point of interests.
 *
 * The implementation rely on a synchronized sorted set. (No concurrency
 * problem).
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class POIList {

    private final SortedSet<POI> points;
    private final ConcurrentLinkedDeque<POIListener> listeners;
    /**
     * Precision of the searches operations on the POI collection.
     */
    public final double precision = 0.5;

    /**
     * Create an empty list of POI.
     */
    public POIList() {
        points = Collections.synchronizedSortedSet(new TreeSet<POI>(new POIComparator()));
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
     * Fires the listeners when a POI is modified.
     *
     * @param e Event.
     */
    protected void firePOIUpdated(POIEvent e) {
        for (POIListener listener : listeners) {
            listener.poiUpdated(e);
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

    /**
     * Add simply the point to the collection.
     *
     * No checks are made if the point is already present or if there is another
     * point near the new point.
     *
     * @param p POI to add.
     */
    public void addPOI(POI p) {
        points.add(p);

        firePOIAdded(new POIEvent(this, p));
    }

    /**
     * Remove simply the point from the collection.
     *
     * @param p POI to add.
     */
    public void removePOI(POI p) {
        points.remove(p);

        firePOIRemoved(new POIEvent(this, p));
    }

    /**
     * Returns all POI contained between the two designed points.
     *
     * @param x1 X position of the first point.
     * @param y1 Y position of the first point.
     * @param x2 X position of the second point.
     * @param y2 Y position of the second point.
     *
     * @return A list of all POI contained between those two points.
     */
    public SortedSet<POI> getPointsBetween(Double x1, Double y1, Double x2, Double y2) {
        POI p1 = new DummyPOI(x1, y1, EventType.DIVERS);
        POI p2 = new DummyPOI(x2, y2, EventType.DIVERS);

        return points.subSet(p1, p2);
    }

    /**
     * Looks up POIs at the position x/y.
     *
     * As x and y are double, the exacts position cannot be given, so the
     * nearest POI will be returned instead.
     *
     * @param x X position of the point.
     * @param y Y position of the point.
     * @param type Type of the POI.
     *
     * @return A list of POI found at this position.
     */
    public ArrayList<POI> getPOIAt(Double x, Double y, EventType type) {
        POI p1 = new DummyPOI(x - precision, y - precision, type);
        POI p2 = new DummyPOI(x + precision, y + precision, type);

        ArrayList<POI> result = new ArrayList<POI>();

        SortedSet<POI> list = points.subSet(p1, p2);
        for (POI poi : list) {
            if (poi.getType().equals(type)) {
                result.add(poi);
            }
        }

        return result;
    }

    /**
     * Gets the points.
     */
    public SortedSet<POI> getPoints() {
        return points;
    }

    /**
     * Gets the number of POI.
     *
     * @return The number of POI.
     */
    public int getSize() {
        return points.size();
    }

    /**
     * Dummy POI.
     *
     * Used only to make searches operations easier.
     */
    private static class DummyPOI extends SimplePOI {

        public DummyPOI(double positionX, double positionY, EventType type) {
            super(positionX, positionY, type);
        }
    }
}
