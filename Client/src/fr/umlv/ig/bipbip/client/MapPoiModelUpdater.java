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
package fr.umlv.ig.bipbip.client;

import fr.umlv.ig.bipbip.poi.Poi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class MapPoiModelUpdater implements Runnable {

    private final MapPoiModel model;
    private final ServerConnection server;
    private final long updateInterval;
    private final JMapViewer mapViewer;
    private final ConcurrentLinkedDeque<UpdateListener> listeners = new ConcurrentLinkedDeque<UpdateListener>();

    /**
     *
     *
     * @param model
     * @param mapViewer
     * @param server
     * @param updateInterval in millisec
     * @param keepAlive
     */
    public MapPoiModelUpdater(MapPoiModel model, JMapViewer mapViewer, ServerConnection server, long updateInterval) {
        Objects.requireNonNull(model);
        Objects.requireNonNull(mapViewer);
        Objects.requireNonNull(server);

        this.model = model;
        this.server = server;
        this.updateInterval = updateInterval;
        this.mapViewer = mapViewer;
    }

    public void addUpdateListener(UpdateListener listener) {
        Objects.requireNonNull(listener);

        listeners.add(listener);
    }

    public void removeUpdateListener(UpdateListener listener) {
        Objects.requireNonNull(listener);

        listeners.remove(listener);
    }

    public void update() throws IOException {
        try {
            ArrayList<Poi> newPOIs = (ArrayList<Poi>) server.getPois(mapViewer.getPosition());
            Collection<Poi> pois = model.getAllPoi();
            for (Poi poi : pois) {
                if (!newPOIs.contains(poi)) {
                    model.removePoi(poi);
                }
            }
            for (Poi poi : newPOIs) {
                if (!pois.contains(poi)) {
                    model.addPoi(poi);
                }
            }

            for (UpdateListener listener : listeners) {
                listener.updated(new UpdateEvent(this, null));
            }
        } catch (IOException e) {
            IOException ex = new IOException("Connection problem: Unable to update POIs", e);
            for (UpdateListener listener : listeners) {
                listener.updateFailed(new UpdateEvent(this, ex));
            }
            throw ex;
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                update();
            } catch (IOException e) {
            }
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
