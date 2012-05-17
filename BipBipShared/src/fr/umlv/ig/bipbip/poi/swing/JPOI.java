/*
 * Copyright (C) 2012 Damien Girard <dgirard@nativesoft.fr>
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
package fr.umlv.ig.bipbip.poi.swing;

import fr.umlv.ig.bipbip.poi.POI;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.ImageIcon;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * A graphical point of interest.
 *
 * Displayable in JMapViewer.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class JPOI implements MapMarker{

    private final POI poi;

    /**
     * Creates a graphical POI displayable on JMapViewer.
     * @param poi Point of interest.
     */
    public JPOI(POI poi) {
        this.poi = poi;
    }

    /**
     * Returns the linked POI.
     */
    public POI getPoi() {
        return poi;
    }

    @Override
    public double getLat() {
        return poi.getY();
    }

    @Override
    public double getLon() {
        return poi.getX();
    }

    @Override
    public void paint(Graphics g, Point position) {
        // Getting the imageIcon.
        ImageIcon img = POIImageFactory.getImage(poi.getType());
        
        // Drawing the POI.
        g.drawImage(img.getImage(), (int)(position.x - (img.getIconWidth() / 2.0)), (int)(position.y - (img.getIconHeight() / 2.0)), null);
        
        // Done.
    }
}
