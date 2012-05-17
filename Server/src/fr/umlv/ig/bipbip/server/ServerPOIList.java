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

import fr.umlv.ig.bipbip.poi.POI;
import fr.umlv.ig.bipbip.poi.POIEvent;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * List of POI, specialized for the server operations.
 *
 * addPOI increment the number of confirmations if the POI already exists in the
 * area.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class ServerPOIList extends POIList {

    // Debug logger.
    private static final Logger logger = Logger.getLogger("fr.umlv.ig.bipbip.server.ServerPOIList");
    
    /**
     * After X refusals, delete the POI.
     */
    public final int deleteAfterRefusals = 3;
    /**
     * When a POI is added, the server is looking for already existing POI near
     * the position with a precision of this constant.
     *
     * If a POI is found, then the number of confirmation is increased.
     *
     * @see #addPOI(fr.umlv.ig.bipbip.poi.POI)
     */
    public final int addPrecision = 10;

    /**
     * Increment the number of refusal of a POI.
     *
     * If the number of refusals > deleteAfterRefusals, then the POI is removed.
     *
     * @param p Point of interest.
     *
     * @see #deleteAfterRefusals
     */
    public void notSeen(POI p) {
        ArrayList<POI> list = getPOIAt(p.getX(), p.getY(), p.getType());
        if (list.isEmpty()) // POI not found.
        {
            logger.log(Level.INFO, "Not seen: Request of a POI not found. x: {0} y: {1}", new Object[]{p.getX(), p.getY()});
            return;
        }

        POI poiToUse = null;

        for (POI poi : list) {
            if (poi.getDate().equals(p.getDate())) {
                // POI found.
                poiToUse = poi;
                break;
            }
        }

        if (poiToUse == null) {
            logger.log(Level.INFO, "Not seen: Request of a POI not found (POI found but invalid date). {0}", p);
            return;
        }

        // POI get, marking it as notSeen.
        int refusals = poiToUse.getRefusals() + 1;
        if (refusals >= deleteAfterRefusals) {
            removePOI(poiToUse);
        } else {
            poiToUse.setRefusals(refusals);
            firePOIUpdated(new POIEvent(this, poiToUse));
        }

        // OK.
    }

    /**
     * Adds a POI.
     *
     * If the POI already exists, then the number of confirmations is increased.
     *
     * @param p POI to add.
     *
     * @see #addPrecision
     */
    @Override
    public void addPOI(POI p) {
        SortedSet<POI> pointsBetween = getPointsBetween(p.getX() - addPrecision, p.getY() - addPrecision, p.getX() + addPrecision, p.getY() + addPrecision);

        if (pointsBetween.isEmpty()) {
            super.addPOI(p);
            return;
        }

        // POI founds, checking for the type and incrementing the number of confirmations.
        for (POI poi : pointsBetween) {
            if (poi.getType().equals(p.getType())) {
                logger.log(Level.FINE, "Confirmation of {0}", poi);
                poi.setConfirmations(poi.getConfirmations() + 1);
                firePOIUpdated(new POIEvent(this, poi));
                return;
            }
        }

        // POI of the type not found, adding a new one so.
        logger.log(Level.FINE, "POI found, but with a different type. Adding the POI so {0}", p);
        super.addPOI(p);
    }
}
