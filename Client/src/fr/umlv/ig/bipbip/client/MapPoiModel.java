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
import fr.umlv.ig.bipbip.poi.PoiEvent;
import fr.umlv.ig.bipbip.poi.PoiListener;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class MapPoiModel implements PoiModel {

    private final ConcurrentLinkedDeque<Poi> pois = new ConcurrentLinkedDeque<Poi>();
    private final ConcurrentLinkedDeque<PoiListener> listeners = new ConcurrentLinkedDeque<PoiListener>();
    
    @Override
    public Collection<Poi> getAllPoi() {
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
    public void addPoi(Poi poi) {
        Objects.requireNonNull(poi);
        
        pois.add(poi);
        firePoiAdded(new PoiEvent(this, poi));
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
}