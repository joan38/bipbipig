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

import fr.umlv.ig.bipbip.Event;
import fr.umlv.ig.bipbip.EventType;
import java.util.Date;

/**
 * Represent a point of interest.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public interface POI {

    /**
     * Gets the X position of the POI.
     *
     * @return The X position.
     */
    double getX();

    /**
     * Gets the Y position of the POI.
     *
     * @return The position.
     */
    double getY();

    /**
     * Gets the type of the POI.
     *
     * @return The type of the POI.
     */
    EventType getType();

    /**
     * Gets the date of the POI.
     *
     * @return The date of the POI.
     */
    Date getDate();

    /**
     * Gets the number of confirmations of this POI.
     *
     * @return The number of confirmations.
     */
    int getConfirmations();
    
    /**
     * Gets the number of persons that marked this POI not seen.
     * 
     * @return The number of refusals.
     */
    int getRefusals();
    
    /**
     * Sets the number of confirmations of this POI.
     * 
     * @param confirmations The number of confirmations.
     */
    void setConfirmations(int confirmations);
    
    /**
     * Sets the number of refusals of this POI.
     * 
     * @param refusals The number of refuses.
     */
    void setRefusals(int refusals);
    
    /**
     * Returns a new event from this POI.
     * 
     * @return An event.
     */
    Event toEvent();
}
