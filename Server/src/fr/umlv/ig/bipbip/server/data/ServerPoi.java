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
import fr.umlv.ig.bipbip.poi.PoiType;
import java.util.Date;

/**
 * Poi that log the number of confirmations
 * 
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class ServerPoi implements Poi{
    
    private final Poi poi;
    

    public ServerPoi(Poi poi) {
        this.poi = poi;
    }

    @Override
    public double getLat() {
        return poi.getLat();
    }

    @Override
    public double getLon() {
        return poi.getLon();
    }

    @Override
    public PoiType getType() {
        return poi.getType();
    }

    @Override
    public Date getDate() {
        return poi.getDate();
    }

    @Override
    public int getConfirmations() {
        return poi.getConfirmations();
    }

    @Override
    public int getRefutations() {
        return getRefutations();
    }

    @Override
    public void setConfirmations(int confirmations) {
        poi.setConfirmations(confirmations);
    }

    @Override
    public void setNbNotSeen(int refutations) {
        poi.setNbNotSeen(refutations);
    }
    
    
}
