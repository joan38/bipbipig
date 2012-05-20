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
import fr.umlv.ig.bipbip.poi.swing.JPoi;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public final class JMap extends JMapViewer implements PoiListener {

    PoiModel model;
    HashMap<Poi, JPoi> poiToJPOI = new HashMap<Poi, JPoi>();

    public JMap(PoiModel model) {
        super();
        Objects.requireNonNull(model);

        this.model = model;
        Collection<Poi> pois = model.getAllPoi();
        for (Poi poi : pois) {
            poiAdded(new PoiEvent(this, poi));
        }
        model.addPoiListener(this);
    }

    @Override
    public void poiAdded(PoiEvent e) {
        Objects.requireNonNull(e);

        Poi poi = e.getPoi();
        JPoi jpoi = new JPoi(poi);
        poiToJPOI.put(poi, jpoi);
        addMapMarker(jpoi);
    }

    @Override
    public void poiRemoved(PoiEvent e) {
        Objects.requireNonNull(e);
        
        Poi poi = e.getPoi();
        removeMapMarker(poiToJPOI.get(poi));
        poiToJPOI.remove(poi);
    }

    @Override
    public void poiUpdated(PoiEvent e) {
        repaint();
    }
}
