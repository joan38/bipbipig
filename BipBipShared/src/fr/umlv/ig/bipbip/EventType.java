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
package fr.umlv.ig.bipbip;

import fr.umlv.ig.bipbip.poi.POI;
import fr.umlv.ig.bipbip.poi.defined.*;
import java.util.Date;

/**
 * Type of available events.
 *
 * Contains the image used to display the event too.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public enum EventType {

    RADAR_FIXE("radar_fixe.png"),
    RADAR_MOBILE("radar_mobile.png"),
    ACCIDENT("accident.png"),
    TRAVAUX("travaux.png"),
    DIVERS("divers.png");
    private final String imageName;

    /**
     * Creates an event.
     */
    private EventType(String imagePath) {
        this.imageName = imagePath;
    }

    /**
     * Gets the name of the image.
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Returns a new POI of the type EventType.
     *
     * @return A POI of the type EventType.
     */
    public POI constructPOI(double positionX, double positionY, Date date) {
        switch (this) {
            case ACCIDENT:
                return new Accident(positionX, positionY, date);
            case DIVERS:
                return new Divers(positionX, positionY, date);
            case RADAR_FIXE:
                return new RadarFixe(positionX, positionY, date);
            case RADAR_MOBILE:
                return new RadarMobile(positionX, positionY, date);
            case TRAVAUX:
                return new Travaux(positionX, positionY, date);
            default:
                throw new UnsupportedOperationException("Unknown Event Type.");
        }
    }
}
