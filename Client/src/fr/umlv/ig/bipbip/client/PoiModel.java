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
package fr.umlv.ig.bipbip.client;

import fr.umlv.ig.bipbip.poi.Poi;
import fr.umlv.ig.bipbip.poi.PoiListener;
import java.util.Collection;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public interface PoiModel {

    /**
     * Gets all POIs.
     */
    public Collection<Poi> getAllPoi();

    /**
     * Adds a POI listener to the collection.
     *
     * @param listener The POIListener to be added.
     */
    public void addPoiListener(PoiListener listener);

    /**
     * Removes a POI listener from the collection.
     *
     * @param listener The POIListener to be removed.
     */
    public void removePoiListener(PoiListener listener);
}
