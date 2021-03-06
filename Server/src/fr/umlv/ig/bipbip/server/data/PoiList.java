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
package fr.umlv.ig.bipbip.server.data;

import fr.umlv.ig.bipbip.poi.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Generic collection of point of interests.
 *
 * The implementation rely on a synchronized array list. (No concurrency
 * problem).
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiList {

    /**
     * Contains the currently active POIs.
     */
    private final List<Poi> activePoints = Collections.synchronizedList(new ArrayList<Poi>());
    /**
     * Contains all the removed POIs.
     */
    private final List<Poi> removedPoints = Collections.synchronizedList(new ArrayList<Poi>());
    private final ConcurrentLinkedQueue<PoiListener> listeners = new ConcurrentLinkedQueue<PoiListener>();
    /**
     * Precision of the searches operations on the POI collection.
     *
     * In meter
     */
    public static final double PRECISION = 500;
    /**
     * Number of meter in one mile.
     *
     * 1 mile = 1852 m
     */
    private static final int NB_METER_OF_NAUTICAL_MILE = 1852;

    /**
     * Get the distance from the first point to the second point in meter.
     *
     * @param latitude1 latitude position of the first point.
     * @param longitude1 longitude position of the first point.
     * @param latitude2 latitude position of the second point.
     * @param longitude2 longitude position of the second point.
     * @return The distance in meter
     */
    public static double getDistanceInMeter(double latitude1, double longitude1, double latitude2, double longitude2) {
        return NB_METER_OF_NAUTICAL_MILE * 60 * Math.acos(Math.sin(latitude1) * Math.sin(latitude2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.cos(longitude2 - longitude1));
    }

    /**
     * Create an empty list of POI.
     */
    public PoiList() {
    }

    /**
     * Adds a POI listener to the collection.
     *
     * @param listener The PoiListener to be added.
     */
    public void addPoiListener(PoiListener listener) {
        Objects.requireNonNull(listener);

        listeners.add(listener);
    }

    /**
     * Removes a POI listener from the collection.
     *
     * @param listener The PoiListener to be removed.
     */
    public void removePoiListener(PoiListener listener) {
        Objects.requireNonNull(listener);

        listeners.remove(listener);
    }

    /**
     * Fires the listeners when a POI is added.
     *
     * @param e Event.
     */
    protected void firePoiAdded(final PoiEvent e) {
        for (final PoiListener listener : listeners) {
            listener.poiAdded(e);
        }
    }

    /**
     * Fires the listeners when a POI is modified.
     *
     * @param e Event.
     */
    protected void firePoiUpdated(final PoiEvent e) {
        for (final PoiListener listener : listeners) {
            listener.poiUpdated(e);
        }
    }

    /**
     * Fires the listeners when a POI is removed.
     *
     * @param e Event.
     */
    protected void firePoiRemoved(final PoiEvent e) {
        for (final PoiListener listener : listeners) {
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
    public void addPoi(Poi p) {
        activePoints.add(p);

        firePoiAdded(new PoiEvent(this, p));
    }

    /**
     * Remove simply the point from the collection.
     *
     * @param p POI to add.
     */
    public void removePoi(Poi p) {
        activePoints.remove(p);

        p.setRemovedDate(new Date());
        removedPoints.add(p);

        firePoiRemoved(new PoiEvent(this, p));
    }

    /**
     * Looks up POIs around the position with the specified POI type.
     *
     * Get all POIs in the area which have the specified POI type.
     *
     * @param latitude1 latitude position of the point.
     * @param longitude1 longitude position of the point.
     * @param radiusArea radius of the area.
     * @param type Type of the POI. Can be null to avoid the filter on this
     * attribut.
     * @param date Date of the POI. Can be null to avoid the filter on this
     * attribut.
     *
     * @return A list of all POI contained in the area.
     */
    public ArrayList<Poi> getPoisInArea(double latitude, double longitude, double radiusArea, PoiType type, Date date) {
        ArrayList<Poi> result = new ArrayList<Poi>();
        synchronized (activePoints) {
            for (Poi poi : activePoints) {
                double calcDistance = getDistanceInMeter(latitude, longitude, poi.getLat(), poi.getLon());
                if (calcDistance <= radiusArea && (type == null || poi.getType().equals(type)) && (date == null || poi.getDate().equals(date))) {
                    result.add(poi);
                }
            }
        }
        return result;
    }

    /**
     * Looks up POIs around the position with the specified POI type.
     *
     * Get all POIs in the area which have the specified POI type.
     *
     * @param latitude1 latitude position of the point.
     * @param longitude1 longitude position of the point.
     * @param radiusArea radius of the area.
     * @param type Type of the POI. Can be null to avoid the filter on this
     * attribut.
     *
     * @return A list of all POI contained in the area.
     */
    public ArrayList<Poi> getPoisInArea(double latitude, double longitude, double radiusArea, PoiType type) {
        return getPoisInArea(latitude, longitude, radiusArea, type, null);
    }

    /**
     * Looks up POIs around the position.
     *
     * Get all POIs in the area. If you put 0 in radiusArea you might still get
     * several POI at the same coordinates
     *
     * @param latitude latitude position of the point.
     * @param longitude longitude position of the point.
     * @param radiusArea radius of the area.
     *
     * @return A list of POI contained in the area.
     */
    public ArrayList<Poi> getPoisInArea(double latitude, double longitude, double radiusArea) {
        return getPoisInArea(latitude, longitude, radiusArea, null);
    }

    /**
     * Gets the points.
     */
    public List<Poi> getPois() {
        return activePoints;
    }

    /**
     * Gets the removed points.
     */
    public List<Poi> getRemovedPois() {
        return removedPoints;
    }

    /**
     * Gets all points at a date.
     *
     * @param date Date to display all points.
     * @param outMinDate The minimum date value.
     *
     * Be aware that this method have a complexity of "n".
     *
     * @return A set that contains all points, including the removed one.
     */
    public ArrayList<Poi> getAllPois(Date date, Date outMinDate) {
        ArrayList<Poi> result = new ArrayList<Poi>();

        synchronized (activePoints) {
            for (Poi poi : activePoints) {
                if (poi.getDate().compareTo(date) <= 0) {
                    result.add(poi);
                    if (poi.getDate().compareTo(outMinDate) < 0) {
                        outMinDate.setTime(poi.getDate().getTime());
                    }
                }
            }
        }

        synchronized (removedPoints) {
            for (Poi poi : removedPoints) {
                if (poi.getDate().compareTo(date) <= 0 && poi.getRemovedDate().compareTo(date) >= 0) {
                    result.add(poi);
                    if (poi.getDate().compareTo(outMinDate) < 0) {
                        outMinDate.setTime(poi.getDate().getTime());
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets the number of POI.
     *
     * @return The number of POI.
     */
    public int getSize() {
        return activePoints.size();
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
        activePoints.remove(oldPoi);
        activePoints.add(newPoi);

        this.firePoiRemoved(new PoiEvent(this, oldPoi));
        this.firePoiAdded(new PoiEvent(this, newPoi));
    }

    /**
     * Dummy POI.
     *
     * Used only to make searches operations easier.
     */
    private static class DummyPoi extends AbstractReportedPoi {

        private DummyPoi(double latitude, double longitude) {
            super(latitude, longitude, PoiType.MISCELLANEOUS, new Date());
        }

        private DummyPoi(double latitude, double longitude, PoiType type) {
            super(latitude, longitude, type, new Date());
        }
    }
}
