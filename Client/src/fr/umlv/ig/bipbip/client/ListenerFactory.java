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
import fr.umlv.ig.bipbip.poi.PoiType;
import fr.umlv.ig.bipbip.poi.swing.JPoi;
import java.awt.Point;
import java.awt.event.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public final class ListenerFactory {

    private ListenerFactory() {
    }

    /**
     * Only for listening JMapViewer component !
     *
     * @return
     */
    public static MouseListener getMapInteractionListener(final ServerConnection server) {
        return new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON3) {
                    Point mousePosition = event.getPoint();
                    JMapViewer map = (JMapViewer) event.getComponent();
                    List<MapMarker> mapMarkers = map.getMapMarkerList();
                    for (MapMarker mapMarker : mapMarkers) {
                        JPoi jpoi = (JPoi) mapMarker;
                        if (jpoi.getIconArea().contains(mousePosition)) {
                            PopupFactory.getPOIPopupMenu(jpoi.getPoi(), server).show(map, mousePosition.x, mousePosition.y);
                            return;
                        }
                    }

                    PopupFactory.getMapPopupMenu(map.getPosition(mousePosition), server).show(map, mousePosition.x, mousePosition.y);
                }
            }
        };
    }
    
    public static MouseListener getRefreshButtonListener(final MapPoiModelUpdater mapPOIModelUpdater) {
        return new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                try {
                    mapPOIModelUpdater.update();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }
    
    public static ActionListener getConfirmationButtonListener(final Poi poi, final ServerConnection server) {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    server.submit(poi);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    public static ActionListener getRefutationButtonListener(final Poi poi, final ServerConnection server) {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    server.notSeen(poi);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }
    
    public static ActionListener getSubmitButtonListener(final Coordinate coordinate, final PoiType type, final ServerConnection server) {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    server.submit(type.constructPOI(coordinate.getLat(), coordinate.getLon(), new Date()));
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }
}
