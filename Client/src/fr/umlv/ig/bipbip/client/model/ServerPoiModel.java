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

import fr.umlv.ig.bipbip.client.ServerCommunication;
import fr.umlv.ig.bipbip.poi.Poi;
import fr.umlv.ig.bipbip.poi.PoiEvent;
import java.io.IOException;
import java.nio.channels.UnresolvedAddressException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class ServerPoiModel implements PoiModel, Runnable {

    private ConcurrentLinkedQueue<Poi> pois = new ConcurrentLinkedQueue<Poi>();
    private final ConcurrentLinkedQueue<PoiListener> poiListeners = new ConcurrentLinkedQueue<PoiListener>();
    private final LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
    private final ConcurrentLinkedQueue<PoiCommunicationListener> communicationListener = new ConcurrentLinkedQueue<PoiCommunicationListener>();
    private final ServerCommunication server;
    private final Timer timer = new Timer();

    public ServerPoiModel(ServerCommunication server) {
        this.server = server;
    }

    @Override
    public Collection<Poi> getAll() {
        return pois;
    }

    @Override
    public void addPoiListener(PoiListener listener) {
        Objects.requireNonNull(listener);

        poiListeners.add(listener);
    }

    @Override
    public void removePoiListener(PoiListener listener) {
        Objects.requireNonNull(listener);

        poiListeners.remove(listener);
    }

    public void addPoiCommunicationListener(PoiCommunicationListener listener) {
        Objects.requireNonNull(listener);

        communicationListener.add(listener);
    }

    public void removePoiCommunicationListener(PoiCommunicationListener listener) {
        Objects.requireNonNull(listener);

        communicationListener.remove(listener);
    }

    /**
     * Fires the listeners when a POI is added.
     *
     * @param event Event.
     */
    protected void firePoiAdded(PoiEvent event) {
        Objects.requireNonNull(event);

        for (PoiListener listener : poiListeners) {
            listener.poiAdded(event);
        }
    }

    /**
     * Fires the listeners when a POI is removed.
     *
     * @param event Event.
     */
    protected void firePoiRemoved(PoiEvent event) {
        Objects.requireNonNull(event);

        for (PoiListener listener : poiListeners) {
            listener.poiRemoved(event);
        }
    }

    /**
     * Fires the listeners when a POI is updated.
     *
     * @param event Event.
     */
    protected void firePoiUpdated(PoiEvent event) {
        Objects.requireNonNull(event);

        for (PoiListener listener : poiListeners) {
            listener.poiUpdated(event);
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
    public void addPoi(Poi poi) {
        Objects.requireNonNull(poi);

        pois.add(poi);
        firePoiAdded(new PoiEvent(this, poi));
    }

    public void updatePoi(Poi poi, Poi newPoi) {
        poi.setConfirmations(newPoi.getConfirmations());
        poi.setRefutations(newPoi.getRefutations());
        firePoiUpdated(new PoiEvent(this, poi));
    }

    /**
     * Remove simply the point from the collection.
     *
     * @param p POI to add.
     */
    public void removePoi(Poi poi) {
        Objects.requireNonNull(poi);

        pois.remove(poi);
        firePoiRemoved(new PoiEvent(this, poi));
    }

    public void startAutoUpdating(final JMapViewer map, long updateInterval) {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    update(map.getPosition());
                } catch (InterruptedException e) {
                    this.cancel();
                }
            }
        }, 0, updateInterval);
    }

    public void stopAutoUpdating() {
        timer.cancel();
    }

    public void update(Coordinate coordinate) throws InterruptedException {
        Objects.requireNonNull(coordinate);

        tasks.put(new UpdateTask(coordinate));
    }

    public void submit(Poi poi) throws InterruptedException {
        Objects.requireNonNull(poi);

        tasks.put(new SubmitTask(poi));
    }

    public void notSeen(Poi poi) throws InterruptedException {
        Objects.requireNonNull(poi);

        tasks.put(new NotSeenTask(poi));
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                tasks.take().run();
            }
        } catch (InterruptedException e) {
        }
    }

    private class SubmitTask implements Runnable {

        private final Poi poi;

        private SubmitTask(Poi poi) {
            Objects.requireNonNull(poi);
            
            this.poi = poi;
        }

        @Override
        public void run() {
            try {
                server.submit(poi);
                PoiEvent event = new PoiEvent(this, poi);
                for (PoiCommunicationListener listener : communicationListener) {
                    listener.poiSubmited(event);
                }
            } catch (IOException e) {
                PoiEvent event = new PoiEvent(this, poi, e);
                for (PoiCommunicationListener listener : communicationListener) {
                    listener.unableToSubmitPoi(event);
                }
            }
        }
    }

    private class NotSeenTask implements Runnable {

        private final Poi poi;

        private NotSeenTask(Poi poi) {
            Objects.requireNonNull(poi);
            
            this.poi = poi;
        }

        @Override
        public void run() {
            try {
                server.notSeen(poi);
                PoiEvent event = new PoiEvent(this, poi);
                for (PoiCommunicationListener listener : communicationListener) {
                    listener.poiDeclaredAsNotSeen(event);
                }
            } catch (IOException e) {
                PoiEvent event = new PoiEvent(this, poi, e);
                for (PoiCommunicationListener listener : communicationListener) {
                    listener.unableToDeclarPoiAsNotSeen(event);
                }
            }
        }
    }

    private class UpdateTask implements Runnable {

        private final Coordinate coordinate;

        private UpdateTask(Coordinate coordinate) {
            Objects.requireNonNull(coordinate);
            
            this.coordinate = coordinate;
        }

        @Override
        public void run() {
            try {
                ArrayList<Poi> newPois = (ArrayList<Poi>) server.getPois(coordinate);

                // Delete each old POI
                for (Poi poi : pois) {
                    if (!newPois.contains(poi)) {
                        removePoi(poi);
                    }
                }

                // Add or update each new POI
                for (Poi newPoi : newPois) {
                    boolean found = false;
                    for (Poi poi : pois) {
                        if (newPoi.equals(poi)) {
                            if (newPoi.getConfirmations() != poi.getConfirmations()
                                    || newPoi.getRefutations() != poi.getRefutations()) {
                                updatePoi(poi, newPoi);
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        addPoi(newPoi);
                    }
                }

                PoiEvent event = new PoiEvent(this, null);
                for (PoiCommunicationListener listener : communicationListener) {
                    listener.poisUpdated(event);
                }
            } catch (IOException e) {
                PoiEvent event = new PoiEvent(this, null, e);
                for (PoiCommunicationListener listener : communicationListener) {
                    listener.unableToUpdatePois(event);
                }
            } catch (UnresolvedAddressException e) {
                PoiEvent event = new PoiEvent(this, null, new IOException("Unable to resolve the server address", e));
                for (PoiCommunicationListener listener : communicationListener) {
                    listener.unableToUpdatePois(event);
                }
            }
        }
    }
}
