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
package fr.umlv.ig.bipbip.poi;

import fr.umlv.ig.bipbip.EventType;
import java.util.Date;

/**
 * Simple graphic point of interest.
 */
public abstract class SimplePOI implements POI {

    private final double positionX;
    private final double positionY;
    private final EventType type;
    private final Date date;
    private int confirmations;

    /**
     * Creates a simple point of interest. (POI)
     *
     * @param positionX X position of the POI.
     * @param positionY Y position of the POI.
     * @param type Type of the POI.
     * @param date Date of the POI.
     */
    public SimplePOI(double positionX, double positionY, EventType type, Date date) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.type = type;
        this.date = date;
    }

    /**
     * Creates a simple point of interest. (POI)
     *
     * @param positionX X position of the POI.
     * @param positionY Y position of the POI.
     * @param type Type of the POI.
     *
     * The date of the POI is set to the current time.
     */
    public SimplePOI(double positionX, double positionY, EventType type) {
        this(positionX, positionY, type, new Date());
    }

    /**
     * Gets the type of the POI.
     *
     * @return The type of the POI.
     */
    @Override
    public EventType getType() {
        return type;
    }

    /**
     * Gets the date of the POI.
     *
     * @return The date of the POI.
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * Gets the X position of the POI.
     *
     * @return The X position.
     */
    @Override
    public double getX() {
        return positionX;
    }
    
    /**
     * Gets the Y position of the POI.
     *
     * @return The position.
     */
    @Override
    public double getY() {
        return positionY;
    }

    /**
     * Gets the number of confirmations of this POI.
     * 
     * @return The number of confirmations.
     */
    public int getConfirmations() {
        return confirmations;
    }
}
