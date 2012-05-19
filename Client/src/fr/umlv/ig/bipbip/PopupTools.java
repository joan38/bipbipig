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

import fr.umlv.ig.bipbip.images.ImageFactory;
import fr.umlv.ig.bipbip.poi.POIType;
import fr.umlv.ig.bipbip.poi.swing.POIImageFactory;
import java.awt.Color;
import java.util.EnumMap;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public final class PopupTools {
    
    private static EnumMap<POIType, JPopupMenu> poiPopupMenu = new EnumMap<POIType, JPopupMenu>(POIType.class);;
    private static JPopupMenu mapPopupMenu;
    
    private PopupTools() {}
    
    public static JPopupMenu getPOIPopupMenu(POIType type) {
        Objects.requireNonNull(type);
        
        if (poiPopupMenu.containsKey(type)) {
            return poiPopupMenu.get(type);
        }
        
        JPopupMenu popupMenu = new JPopupMenu();
        String typeName = type.name();
        JLabel signaler = new JLabel(typeName.substring(0, 1).toUpperCase() + typeName.substring(1).toLowerCase());
        signaler.setBorder(new EmptyBorder(1, 10, 5, 0));
        signaler.setForeground(Color.GRAY);
        popupMenu.add(signaler);
        popupMenu.add(new JMenuItem("Declare as missing", ImageFactory.getImage("delete.png")));
        poiPopupMenu.put(type, popupMenu);
        
        return popupMenu;
    }
    
    public static JPopupMenu getMapPopupMenu() {
        if (mapPopupMenu != null) {
            return mapPopupMenu;
        }
        
        mapPopupMenu = new JPopupMenu();
        JLabel signaler = new JLabel("Signaler");
        signaler.setBorder(new EmptyBorder(1, 10, 5, 0));
        signaler.setForeground(Color.GRAY);
        mapPopupMenu.add(signaler);
        mapPopupMenu.add(new JMenuItem("Fixed speed cam", POIImageFactory.getImage(POIType.FIXED_SPEED_CAM)));
        mapPopupMenu.add(new JMenuItem("Mobile speed cam", POIImageFactory.getImage(POIType.MOBILE_SPEED_CAM)));
        mapPopupMenu.add(new JMenuItem("Accident", POIImageFactory.getImage(POIType.ACCIDENT)));
        mapPopupMenu.add(new JMenuItem("Roadworks", POIImageFactory.getImage(POIType.ROADWORKS)));
        mapPopupMenu.add(new JMenuItem("Miscellaneous", POIImageFactory.getImage(POIType.MISCELLANEOUS)));
        
        return mapPopupMenu;
    }
}
