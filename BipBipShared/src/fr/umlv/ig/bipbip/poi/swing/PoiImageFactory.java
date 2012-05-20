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

import fr.umlv.ig.bipbip.poi.PoiType;
import java.util.EnumMap;
import javax.swing.ImageIcon;

/**
 * Flyweight class used for image loading.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiImageFactory {

    private static EnumMap<PoiType, ImageIcon> images = new EnumMap<PoiType, ImageIcon>(PoiType.class);

    /**
     * Gets an image of a POI.
     *
     * This is a flyweight class, the image is loaded one time in memory.
     *
     * @param type Type of the POI.
     * @return The image.
     */
    public static ImageIcon getImage(PoiType type) {
        if (images.containsKey(type)) {
            return images.get(type);
        }
        // Loading the image.
        ImageIcon imageIcon = new ImageIcon(PoiImageFactory.class.getResource(type.getImageName()));
        
        images.put(type, imageIcon);
        
        return imageIcon;
    }
}
