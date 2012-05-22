/*
 * Copyright (C) 2012 Joan Goyeau <joan.goyeau@gmail.com>
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
package fr.umlv.ig.bipbip.client.model;

import fr.umlv.ig.bipbip.client.ServerConnection;
import fr.umlv.ig.bipbip.poi.Poi;
import fr.umlv.ig.bipbip.poi.PoiEvent;
import fr.umlv.ig.bipbip.poi.PoiListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class ServerPoiModel implements PoiModel {

    private final ConcurrentLinkedQueue<Poi> pois = new ConcurrentLinkedQueue<Poi>();
    private final ConcurrentLinkedQueue<PoiListener> listeners = new ConcurrentLinkedQueue<PoiListener>();
    private final ServerConnection server;

    public ServerPoiModel(ServerConnection server) {
        this.server = server;
    }

    public void update(Coordinate coordinate) throws IOException {
        try {
            ArrayList<Poi> newPOIs = (ArrayList<Poi>) server.getPois(coordinate);
            for (Poi poi : pois) {
                if (!newPOIs.contains(poi)) {
                    remove(poi);
                }
            }
            for (Poi poi : newPOIs) {
                if (!pois.contains(poi)) {
                    add(poi);
                }
            }
        } catch (IOException e) {
            throw new IOException("Connection problem: Unable to update POIs", e);
        }
    }

    @Override
    public Collection<Poi> getAll() {
        return pois;
    }

    @Override
    public void addPoiListener(PoiListener listener) {
        Objects.requireNonNull(listener);

        listeners.add(listener);
    }

    @Override
    public void removePoiListener(PoiListener listener) {
        Objects.requireNonNull(listener);

        listeners.remove(listener);
    }

    /**
     * Fires the listeners when a POI is added.
     *
     * @param e Event.
     */
    protected void firePoiAdded(PoiEvent e) {
        Objects.requireNonNull(e);

        for (PoiListener listener : listeners) {
            listener.poiAdded(e);
        }
    }

    /**
     * Fires the listeners when a POI is removed.
     *
     * @param e Event.
     */
    protected void firePoiRemoved(PoiEvent e) {
        Objects.requireNonNull(e);

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
    public void add(Poi poi) {
        Objects.requireNonNull(poi);

        pois.add(poi);
        firePoiAdded(new PoiEvent(this, poi));
    }

    /**
     * Remove simply the point from the collection.
     *
     * @param p POI to add.
     */
    public void remove(Poi poi) {
        Objects.requireNonNull(poi);

        pois.remove(poi);
        firePoiRemoved(new PoiEvent(this, poi));
    }

    public void submit(Poi poi) throws IOException {
        Objects.requireNonNull(poi);

        try {
            server.submit(poi);
            update(new Coordinate(poi.getLat(), poi.getLon()));
        } catch (IOException e) {
            throw new IOException("Connection problem: Unable to submit the POI", e);
        }
    }

    public void notSeen(Poi poi) throws IOException {
        Objects.requireNonNull(poi);

        try {
            server.notSeen(poi);
            update(new Coordinate(poi.getLat(), poi.getLon()));
        } catch (IOException e) {
            throw new IOException("Connection problem: Unable to declare the POI as not seen", e);
        }
    }
}
