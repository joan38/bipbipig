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
package fr.umlv.ig.bipbip.server.data;

import fr.umlv.ig.bipbip.poi.Poi;
import java.util.Comparator;

/**
 * Class used to compare POI.
 *
 * A POI is greater than another one only if its X position and Y position is
 * higher than the other one.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiComparatorSouthEast implements Comparator<Poi> {

    @Override
    public int compare(Poi o1, Poi o2) {
        if (o1.getLat() < o2.getLat() && o1.getLon() > o2.getLon()) {
            return 1;
        } else if (o1.getLat() == o2.getLat() && o1.getLon() == o2.getLon()) {
            return 0;
        } else {
            return -1;
        }
    }
}
