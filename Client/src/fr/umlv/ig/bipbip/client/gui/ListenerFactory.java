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
package fr.umlv.ig.bipbip.client.gui;

import fr.umlv.ig.bipbip.client.model.ServerPoiModel;
import fr.umlv.ig.bipbip.poi.Poi;
import fr.umlv.ig.bipbip.poi.PoiType;
import fr.umlv.ig.bipbip.poi.swing.JPoi;
import java.awt.Point;
import java.awt.event.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.swing.JLabel;
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
    public static MouseListener getMapInteractionListener(final ServerPoiModel model) {
        Objects.requireNonNull(model);

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
                            PopupFactory.getPoiPopupMenu(jpoi.getPoi(), model, map).show(map, mousePosition.x, mousePosition.y);
                            return;
                        }
                    }

                    PopupFactory.getMapPopupMenu(map.getPosition(mousePosition), model, map).show(map, mousePosition.x, mousePosition.y);
                }
            }
        };
    }

    public static MouseListener getRefreshButtonListener(final ServerPoiModel model, final JMapViewer map, final JLabel infos) {
        Objects.requireNonNull(model);
        Objects.requireNonNull(map);
        Objects.requireNonNull(infos);

        return new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                try {
                    model.update(map.getPosition());
                } catch (InterruptedException e) {
                }
            }
        };
    }

    public static ActionListener getConfirmationButtonListener(final Poi poi, final ServerPoiModel model, final JMapViewer map) {
        Objects.requireNonNull(poi);
        Objects.requireNonNull(model);
        Objects.requireNonNull(map);

        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    model.submit(poi);
                    model.update(map.getPosition());
                } catch (InterruptedException e) {
                }
            }
        };
    }

    public static ActionListener getNotSeenButtonListener(final Poi poi, final ServerPoiModel model, final JMapViewer map) {
        Objects.requireNonNull(poi);
        Objects.requireNonNull(model);
        Objects.requireNonNull(map);

        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    model.notSeen(poi);
                    model.update(map.getPosition());
                } catch (InterruptedException e) {
                }
            }
        };
    }

    public static ActionListener getSubmitButtonListener(final Coordinate coordinate, final PoiType type, final ServerPoiModel model, final JMapViewer map) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(type);
        Objects.requireNonNull(model);
        Objects.requireNonNull(map);

        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    model.submit(type.constructPoi(coordinate.getLat(), coordinate.getLon(), new Date()));
                    model.update(map.getPosition());
                } catch (InterruptedException e) {
                }
            }
        };
    }
}
