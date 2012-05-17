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
package fr.umlv.ig.bipbip.server;

import java.util.EventListener;

/**
 * The POIListener interface for receiving action events. The class that is
 * interested in processing an POI operation event implements this interface,
 * and the object created with that class is registered with a component, using
 * the component's addPOIListener method. When the action event occurs, that
 * object's poiAdded or poiRemoved method is invoked.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public interface POIListener extends EventListener {

    /**
     * Invoked when a POI is added.
     *
     * @param e Event information.
     */
    void poiAdded(POIEvent e);
    
    /**
     * Invoked when a POI is updated.
     * 
     * @param e Event information.
     */
    void poiUpdated(POIEvent e);

    /**
     * Invoked when a POI is removed.
     *
     * @param e Event information.
     */
    void poiRemoved(POIEvent e);
}
