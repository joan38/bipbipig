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
package fr.umlv.ig.bipbip;

import fr.umlv.ig.bipbip.poi.POI;
import fr.umlv.ig.bipbip.poi.POIEvent;
import fr.umlv.ig.bipbip.poi.POIListener;
import fr.umlv.ig.bipbip.poi.POIModel;
import fr.umlv.ig.bipbip.poi.swing.JPOI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public final class JMap extends JMapViewer implements POIListener {

    POIModel model;
    HashMap<POI, JPOI> poiToJPOI = new HashMap<POI, JPOI>();

    public JMap(POIModel model) {
        super();
        Objects.requireNonNull(model);

        this.model = model;
        ArrayList<POI> pois = model.getAllPOI();
        for (POI poi : pois) {
            poiAdded(new POIEvent(this, poi));
        }
        model.addPOIListener(this);
    }

    @Override
    public void poiAdded(POIEvent e) {
        Objects.requireNonNull(e);

        POI poi = e.getPoi();
        JPOI jpoi = new JPOI(poi);
        poiToJPOI.put(poi, jpoi);
        addMapMarker(jpoi);
    }

    @Override
    public void poiRemoved(POIEvent e) {
        Objects.requireNonNull(e);
        
        POI poi = e.getPoi();
        removeMapMarker(poiToJPOI.get(poi));
        poiToJPOI.remove(poi);
    }

    @Override
    public void poiUpdated(POIEvent e) {
        repaint();
    }
}
