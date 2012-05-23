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

import fr.umlv.ig.bipbip.poi.defined.*;
import java.util.Date;

/**
 * Type of available events.
 *
 * Contains the image used to display the event too.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public enum PoiType {

    FIXED_SPEED_CAM("Fixed speed cam", "fixed_speed_cam.png") {

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date) {
            return new FixedSpeedCam(latitude, longitude, date);
        }

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date, int confirmations) {
            return new FixedSpeedCam(latitude, longitude, date, confirmations);
        }
    },
    MOBILE_SPEED_CAM("Mobile speed cam", "mobile_speed_cam.png") {

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date) {
            return new MobileSpeedCam(latitude, longitude, date);
        }

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date, int confirmations) {
            return new MobileSpeedCam(latitude, longitude, date, confirmations);
        }
    },
    ACCIDENT("Accident", "accident.png") {

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date) {
            return new Accident(latitude, longitude, date);
        }

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date, int confirmations) {
            return new Accident(latitude, longitude, date, confirmations);
        }
    },
    ROADWORKS("Roadworks", "roadworks.png") {

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date) {
            return new RoadWorks(latitude, longitude, date);
        }

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date, int confirmations) {
            return new RoadWorks(latitude, longitude, date, confirmations);
        }
    },
    MISCELLANEOUS("Miscellaneous", "miscellaneous.png") {

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date) {
            return new Miscellaneous(latitude, longitude, date);
        }

        @Override
        public Poi constructPoi(double latitude, double longitude, Date date, int confirmations) {
            return new Miscellaneous(latitude, longitude, date, confirmations);
        }
    };
    private final String title;
    private final String imageName;

    /**
     * Creates an event.
     */
    private PoiType(String title, String imageName) {
        this.title = title;
        this.imageName = imageName;
    }

    /**
     * Gets the name of the image.
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Returns a new POI of the type EventType.
     *
     * @param latitude
     * @param longitude
     * @param date
     * @return A POI of the type EventType.
     */
    public abstract Poi constructPoi(double latitude, double longitude, Date date);

    /**
     * Returns a new POI of the type EventType.
     *
     * @param latitude
     * @param longitude
     * @param date
     * @param confirmations
     * @return A POI of the type EventType.
     */
    public abstract Poi constructPoi(double latitude, double longitude, Date date, int confimations);

    @Override
    public String toString() {
        return title;
    }
}
