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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public final class JMap extends JMapViewer implements POIListener {

    POIModel model;
    HashMap<POI, JPOI> POItoJPOI = new HashMap<POI, JPOI>();
    
    public JMap(POIModel model) {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                List<MapMarker> mapMarkers = getMapMarkerList();
                for (MapMarker mapMarker : mapMarkers) {
                    JPOI jpoi = (JPOI) mapMarker;
                    if (jpoi.getIconArea().contains(getMousePosition())) {
                        System.out.println("Ahah");
                    }
                }
            }
        });
        
        this.model = model;
        ArrayList<POI> pois = model.getAllPOI();
        for (POI poi : pois) {
            poiAdded(new POIEvent(this, poi));
        }
        model.addPOIListener(this);
    }

    @Override
    public void poiAdded(POIEvent e) {
        POI poi = e.getPoi();
        JPOI jpoi = new JPOI(poi);
        POItoJPOI.put(poi, jpoi);
        addMapMarker(jpoi);
    }

    @Override
    public void poiRemoved(POIEvent e) {
        removeMapMarker(POItoJPOI.get(e.getPoi()));
    }
}
