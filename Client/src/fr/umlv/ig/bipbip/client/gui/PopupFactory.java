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
import fr.umlv.ig.bipbip.images.ImageFactory;
import fr.umlv.ig.bipbip.poi.Poi;
import fr.umlv.ig.bipbip.poi.PoiType;
import fr.umlv.ig.bipbip.poi.swing.PoiImageFactory;
import java.awt.Color;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Popup menu creation factory
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public final class PopupFactory {

    private PopupFactory() {
    }

    /**
     * Configure a popup menu for adding new POI on the map
     *
     * @param coordinate
     * @param model
     * @return
     */
    public static JPopupMenu getMapPopupMenu(Coordinate coordinate, ServerPoiModel model, JLabel infos) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(model);
        Objects.requireNonNull(infos);

        JPopupMenu mapPopupMenu = new JPopupMenu();
        JLabel signaler = new JLabel("Signaler");
        signaler.setBorder(new EmptyBorder(1, 10, 1, 0));
        signaler.setForeground(Color.GRAY);
        mapPopupMenu.add(signaler);
        mapPopupMenu.add(new JSeparator());

        PoiType[] types = PoiType.values();
        for (PoiType type : types) {
            JMenuItem poiDeclarationItem = new JMenuItem(type.toString(), PoiImageFactory.getImage(type));
            poiDeclarationItem.addActionListener(ListenerFactory.getSubmitButtonListener(coordinate, type, model, infos));
            mapPopupMenu.add(poiDeclarationItem);
        }

        return mapPopupMenu;
    }

    public static JPopupMenu getPoiPopupMenu(Poi poi, ServerPoiModel model, JLabel infos) {
        Objects.requireNonNull(poi);
        Objects.requireNonNull(model);
        Objects.requireNonNull(infos);

        JPopupMenu popupMenu = new JPopupMenu();
        JLabel signaler = new JLabel(poi.getType().toString());
        signaler.setBorder(new EmptyBorder(1, 10, 1, 0));
        signaler.setForeground(Color.GRAY);
        popupMenu.add(signaler);
        popupMenu.add(new JSeparator());

        JMenuItem confirmation = new JMenuItem("Confirm", ImageFactory.getImage("confirm.png"));
        confirmation.addActionListener(ListenerFactory.getConfirmationButtonListener(poi, model, infos));
        popupMenu.add(confirmation);

        JMenuItem refutation = new JMenuItem("Not seen", ImageFactory.getImage("delete.png"));
        refutation.addActionListener(ListenerFactory.getNotSeenButtonListener(poi, model, infos));
        popupMenu.add(refutation);

        return popupMenu;
    }

    public static SocketAddress requestConnectionAddress(int defaultPort) {
        String input = (String) JOptionPane.showInputDialog(null,
                "Server address : <host>[:port]",
                "Connection",
                JOptionPane.QUESTION_MESSAGE,
                ImageFactory.getImage("confirm.png"),
                null,
                null);

        if (input == null) {
            return null;
        } else if (input.matches("[^\\s]+:\\d{1,5}")) {
            String[] split = input.split(":");
            return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
        } else if (input.matches("[^\\s:]+")) {
            return new InetSocketAddress(input, defaultPort);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Invalid host",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE,
                    ImageFactory.getImage("delete.png"));
            return requestConnectionAddress(defaultPort);
        }
    }
}
