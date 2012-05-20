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
package fr.umlv.ig.bipbip.poi.defined;

import fr.umlv.ig.bipbip.poi.PoiType;
import fr.umlv.ig.bipbip.poi.AbstractReportedPoi;
import java.util.Date;

/**
 * Represent a travaux.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class RoadWorks extends AbstractReportedPoi {

    public RoadWorks(double positionX, double positionY, Date date) {
        super(positionX, positionY, PoiType.ROADWORKS, date);
    }
}
