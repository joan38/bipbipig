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

import java.util.Date;
import java.util.Objects;

/**
 * Point a interest that can be reported by a client.
 */
public abstract class AbstractReportedPoi implements Poi {

    private final double latitude;
    private final double longitude;
    private final PoiType type;
    private final Date date;
    private int confirmations;
    private int refutations;

    /**
     * Creates a reported point of interest. (POI)
     *
     * @param latitude Latitude of the POI.
     * @param longitude Longitude of the POI.
     * @param type Type of the POI.
     * @param date Declaration date of the POI.
     */
    public AbstractReportedPoi(double latitude, double longitude, PoiType type, Date date) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(type);
        
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.date = date;
        this.confirmations = 0;
        this.refutations = 0;
    }

    /**
     * Gets the type of the POI.
     *
     * @return The type of the POI.
     */
    @Override
    public PoiType getType() {
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
     * Gets the latitude of the POI.
     *
     * @return The latitude.
     */
    @Override
    public double getLat() {
        return latitude;
    }

    /**
     * Gets the longitude of the POI.
     *
     * @return The longitude.
     */
    @Override
    public double getLon() {
        return longitude;
    }

    /**
     * Gets the number of persons that marked this POI not seen.
     *
     * @return The number of refutations.
     */
    @Override
    public int getRefutations() {
        return refutations;
    }

    /**
     * Gets the number of confirmations of this POI.
     *
     * @return The number of confirmations.
     */
    @Override
    public int getConfirmations() {
        return confirmations;
    }

    /**
     * Sets the number of confirmations of this POI.
     *
     * @param confirmations The number of confirmations.
     */
    @Override
    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    /**
     * Sets the number of refutation of this POI.
     *
     * @param refutations The number of refuses.
     */
    @Override
    public void setNbNotSeen(int refutations) {
        this.refutations = refutations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractReportedPoi other = (AbstractReportedPoi) obj;
        if (Double.doubleToLongBits(this.latitude) != Double.doubleToLongBits(other.latitude)) {
            return false;
        }
        if (Double.doubleToLongBits(this.longitude) != Double.doubleToLongBits(other.longitude)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        if (this.confirmations != other.confirmations) {
            return false;
        }
        if (this.refutations != other.refutations) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.latitude) ^ (Double.doubleToLongBits(this.latitude) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.longitude) ^ (Double.doubleToLongBits(this.longitude) >>> 32));
        hash = 29 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 29 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 29 * hash + this.confirmations;
        hash = 29 * hash + this.refutations;
        return hash;
    }

    @Override
    public String toString() {
        return "POI, x: " + latitude + " y: " + longitude + " date: " + date + " type: " + type;
    }
}
