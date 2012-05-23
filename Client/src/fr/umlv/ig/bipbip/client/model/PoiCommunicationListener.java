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
package fr.umlv.ig.bipbip.client.model;

import fr.umlv.ig.bipbip.poi.PoiEvent;
import java.util.EventListener;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public interface PoiCommunicationListener extends EventListener {

    public void poiSubmited(PoiEvent event);
    
    public void poiDeclaredAsNotSeen(PoiEvent event);
    
    public void poisUpdated(PoiEvent event);
    
    public void unableToSubmitPoi(PoiEvent event);

    public void unableToDeclarPoiAsNotSeen(PoiEvent event);

    public void unableToUpdatePois(PoiEvent event);
}
