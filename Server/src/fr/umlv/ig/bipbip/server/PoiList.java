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

import fr.umlv.ig.bipbip.poi.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Generic collection of point of interests.
 *
 * The implementation rely on a synchronized sorted set. (No concurrency
 * problem).
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiList {

    private final SortedSet<Poi> points = Collections.synchronizedSortedSet(new TreeSet<Poi>(new PoiComparator()));
    private final ConcurrentLinkedQueue<PoiListener> listeners = new ConcurrentLinkedQueue<PoiListener>();
    /**
     * Precision of the searches operations on the POI collection.
     */
    public static final double PRECISION = 0.5;

    /**
     * Create an empty list of POI.
     */
    public PoiList() {
    }

    /**
     * Adds a POI listener to the collection.
     *
     * @param listener The POIListener to be added.
     */
    public void addPOIListener(PoiListener listener) {
        Objects.requireNonNull(listener);

        listeners.add(listener);
    }

    /**
     * Removes a POI listener from the collection.
     *
     * @param listener The POIListener to be removed.
     */
    public void removePOIListener(PoiListener listener) {
        Objects.requireNonNull(listener);

        listeners.remove(listener);
    }

    /**
     * Fires the listeners when a POI is added.
     *
     * @param e Event.
     */
    protected void firePOIAdded(PoiEvent e) {
        for (PoiListener listener : listeners) {
            listener.poiAdded(e);
        }
    }

    /**
     * Fires the listeners when a POI is modified.
     *
     * @param e Event.
     */
    protected void firePOIUpdated(PoiEvent e) {
        for (PoiListener listener : listeners) {
            listener.poiUpdated(e);
        }
    }

    /**
     * Fires the listeners when a POI is removed.
     *
     * @param e Event.
     */
    protected void firePOIRemoved(PoiEvent e) {
        for (PoiListener listener : listeners) {
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
    public void addPOI(Poi p) {
        points.add(p);

        firePOIAdded(new PoiEvent(this, p));
    }

    /**
     * Remove simply the point from the collection.
     *
     * @param p POI to add.
     */
    public void removePOI(Poi p) {
        points.remove(p);

        firePOIRemoved(new PoiEvent(this, p));
    }

    /**
     * Returns all POI contained between the two designed points.
     *
     * @param latitude1 X position of the first point.
     * @param longitude1 Y position of the first point.
     * @param latitude2 X position of the second point.
     * @param longitude2 Y position of the second point.
     *
     * @return A list of all POI contained between those two points.
     */
    public SortedSet<Poi> getPointsBetween(double latitude1, double longitude1, double latitude2, double longitude2) {
        Poi p1 = new DummyPOI(latitude1, longitude1, PoiType.MISCELLANEOUS);
        Poi p2 = new DummyPOI(latitude2, longitude2, PoiType.MISCELLANEOUS);

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
    public ArrayList<Poi> getPOIAt(Double x, Double y, PoiType type) {
        Poi p1 = new DummyPOI(x - PRECISION, y - PRECISION, type);
        Poi p2 = new DummyPOI(x + PRECISION, y + PRECISION, type);

        ArrayList<Poi> result = new ArrayList<Poi>();

        SortedSet<Poi> list = points.subSet(p1, p2);
        for (Poi poi : list) {
            if (poi.getType().equals(type)) {
                result.add(poi);
            }
        }

        return result;
    }

    /**
     * Gets the points.
     */
    public SortedSet<Poi> getPoints() {
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
     * Updates (replace) a POI in the collection.
     *
     * Removes the previous Poi and add the new one at the right place. Keeps
     * the sortedSet in a consistent state.
     *
     * Fires a removed and added events.
     */
    public void updatePoi(Poi oldPoi, Poi newPoi) {
        points.remove(oldPoi);
        points.add(newPoi);

        this.firePOIRemoved(new PoiEvent(this, oldPoi));
        this.firePOIAdded(new PoiEvent(this, newPoi));
    }

    /**
     * Dummy POI.
     *
     * Used only to make searches operations easier.
     */
    private static class DummyPOI extends AbstractReportedPoi {

        private DummyPOI(double positionX, double positionY, PoiType type) {
            super(positionX, positionY, type, new Date());
        }
    }
}
