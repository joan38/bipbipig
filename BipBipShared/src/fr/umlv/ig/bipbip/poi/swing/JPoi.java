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

import fr.umlv.ig.bipbip.images.ImageFactory;
import fr.umlv.ig.bipbip.poi.Poi;
import java.awt.*;
import javax.swing.ImageIcon;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * A graphical point of interest.
 *
 * Displayable in JMapViewer.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class JPoi implements MapMarker {

    private final Poi poi;
    private Rectangle iconArea;

    /**
     * Creates a graphical POI displayable on JMapViewer.
     * @param poi Point of interest.
     */
    public JPoi(Poi poi) {
        this.poi = poi;
    }

    /**
     * Returns the aera position of the icon.
     */
    public Rectangle getIconArea() {
        return iconArea;
    }

    /**
     * Returns the linked POI.
     */
    public Poi getPoi() {
        return poi;
    }

    @Override
    public double getLat() {
        return poi.getLat();
    }

    @Override
    public double getLon() {
        return poi.getLon();
    }

    @Override
    public void paint(Graphics g, Point position) {
        // Getting the imageIcon.
        ImageIcon img = PoiImageFactory.getImage(poi.getType());
        int imgWidth = img.getIconWidth();
        int imgHeight = img.getIconHeight();
        int x = position.x - imgWidth/2;
        int y = position.y - imgHeight/2;
        
        iconArea = new Rectangle(x, y, imgWidth, imgHeight);
        g.drawImage(img.getImage(), x, y, null);
        
        // Confirmations
        Integer confirmations = poi.getConfirmations();
        
        // Pastil
        x = position.x + imgWidth/2 - 11;
        y = position.y - imgHeight/2 - 8;
        if (confirmations < 10) {
            g.drawImage(ImageFactory.getImage("pastil_1_digit.png").getImage(), x, y, null);
        } else if (confirmations < 100) {
            g.drawImage(ImageFactory.getImage("pastil_2_digit.png").getImage(), x, y, null);
        } else {
            g.drawImage(ImageFactory.getImage("pastil_2_digit.png").getImage(), x, y, null);
            confirmations = 99;
        }
        
        // Number
        x = position.x + imgWidth/2 - 5;
        y = position.y - imgHeight/2 + 7;
        g.setFont(new Font(null, Font.BOLD, 12));
        g.setColor(Color.WHITE);
        g.drawString(confirmations.toString(), x, y);
    }
}
