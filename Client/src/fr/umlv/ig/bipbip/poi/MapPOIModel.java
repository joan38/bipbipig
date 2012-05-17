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
package fr.umlv.ig.bipbip.poi;

import fr.umlv.ig.bipbip.ClientCommand;
import fr.umlv.ig.bipbip.Event;
import fr.umlv.ig.bipbip.NetUtil;
import fr.umlv.ig.bipbip.ServerCommandHandler;
import fr.umlv.ig.bipbip.poi.POI;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class MapPOIModel implements POIModel {

    private final ArrayList<POI> pois = new ArrayList<POI>();
    private final ConcurrentLinkedDeque<POIListener> listeners = new ConcurrentLinkedDeque<POIListener>();
    
    @Override
    public ArrayList<POI> getAllPOI() {
        return pois;
    }

    @Override
    public void addPOIListener(POIListener listener) {
        Objects.requireNonNull(listener);

        listeners.add(listener);
    }

    @Override
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
        Objects.requireNonNull(e);
        
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
        Objects.requireNonNull(e);
        
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
    public void addPOI(POI poi) {
        Objects.requireNonNull(poi);
        
        pois.add(poi);
        firePOIAdded(new POIEvent(this, poi));
    }
    
    /**
     * Remove simply the point from the collection.
     *
     * @param p POI to add.
     */
    public void removePOI(POI poi) {
        Objects.requireNonNull(poi);
        
        pois.remove(poi);
        firePOIRemoved(new POIEvent(this, poi));
    }
}
