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
import fr.umlv.ig.bipbip.poi.PoiEvent;
import fr.umlv.ig.bipbip.poi.PoiType;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.*;

/**
 * List of POI, specialized for the server operations.
 *
 * addPOI increment the number of confirmations if the POI already exists in the
 * area.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class ServerPoiList extends PoiList {
    // Debug logger.

    private static final Logger logger = Logger.getLogger(PoiList.class.getName());
    /**
     * After X refutations, delete the POI.
     */
    public static final int NB_REFUTATION_FOR_DELETE = 3;

    /**
     * Increment the number of refutation of a POI.
     *
     * If the number of refutations > NB_REFUTATION_FOR_DELETE, then the POI is
     * removed.
     *
     * @param p Point of interest.
     *
     * @see #NB_REFUTATION_FOR_DELETE
     */
    public void notSeen(Poi p) {
        ArrayList<Poi> list = getPoiAt(p.getLat(), p.getLon(), p.getType());
        if (list.isEmpty()) // POI not found.
        {
            logger.log(Level.INFO, "Not seen: Request of a POI not found. x: {0} y: {1}", new Object[]{p.getLat(), p.getLon()});
            return;
        }

        Poi poiToUse = null;

        for (Poi poi : list) {
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
        int refutations = poiToUse.getRefutations() + 1;
        if (refutations >= NB_REFUTATION_FOR_DELETE) {
            removePoi(poiToUse);
        } else {
            poiToUse.setNbNotSeen(refutations);
            firePoiUpdated(new PoiEvent(this, poiToUse));
        }

        // OK.
    }
    /**
     * Version of the xml file.
     */
    private static final Integer xmlVersion = 1;

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
    public void addPoi(Poi p) {
        ArrayList<Poi> pointsBetween = getPoiAt(p.getLat(), p.getLon(), p.getType());

        if (pointsBetween.isEmpty()) { 
            super.addPoi(p);
            return;
        }

        // POI founds, checking for the type and incrementing the number of confirmations.
        for (Poi poi : pointsBetween) {
                logger.log(Level.FINE, "Confirmation of "+ poi);
                poi.setConfirmations(poi.getConfirmations() + 1);
                firePoiUpdated(new PoiEvent(this, poi));
                return;
        }

        // POI of the type not found, adding a new one so.
        logger.log(Level.FINE, "POI found, but with a different type. Adding the POI so "+ p);
        super.addPoi(p);
    }

    /**
     * Save the list of POI to a XML file.
     *
     * @param output Output stream to write.
     */
    public void saveToFile(OutputStream output) throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(output, "UTF8");

        writer.writeStartDocument("UTF-8", "1.0");

        writer.writeStartElement("points");
        writer.writeAttribute("version", xmlVersion.toString());

        // Date formatter.
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);

        // Writing the POIs.
        writePoi(writer, dateFormat, this.getPoints());
        writePoi(writer, dateFormat, this.getRemovedPoints());

        writer.writeEndElement();

        writer.writeEndDocument();

        writer.flush();
        writer.close();
    }

    private void writePoi(XMLStreamWriter writer, DateFormat dateFormat, List<Poi> points) throws XMLStreamException {
        for (Poi poi : points) {
            writer.writeStartElement("poi");
            writer.writeAttribute("type", poi.getType().name());
            writer.writeAttribute("latitude", ((Double) poi.getLat()).toString());
            writer.writeAttribute("longitude", ((Double) poi.getLon()).toString());
            writer.writeAttribute("date", dateFormat.format(poi.getDate()));

            writer.writeStartElement("confirmations");
            writer.writeCharacters(((Integer) poi.getConfirmations()).toString());
            writer.writeEndElement();

            writer.writeStartElement("refutations");
            writer.writeCharacters(((Integer) poi.getRefutations()).toString());
            writer.writeEndElement();

            if (poi.getRemovedDate() != null) {
                writer.writeStartElement("removedDate");
                writer.writeCharacters(dateFormat.format(poi.getRemovedDate()));
                writer.writeEndElement();
            }

            writer.writeEndElement();
        }
    }

    public static ServerPoiList readFromFile(FileInputStream input) throws XMLStreamException, Exception {
        ServerPoiList poiList = new ServerPoiList();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(input, "UTF8");

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        Poi currentPoi = null;

        // Parsing, gooo!
        // As this fckin StaX does not support XSD, and I do not have the time to
        // rewrite everything, I do some sanity check in this code.
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamConstants.START_ELEMENT:
                    if (reader.getLocalName().equals("points")) {
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            if (reader.getAttributeLocalName(i).equals("version")) {
                                // Checking version.
                                if (reader.getAttributeValue(i).equals(xmlVersion.toString()) == false) {
                                    throw new Exception("Invalid file version! Found: " + reader.getAttributeValue(i) + " Expected: " + xmlVersion.toString());
                                }
                            }
                        }
                    } else if (reader.getLocalName().equals("poi")) {
                        // New Poi.
                        Double latitude = 0.0, longitude = 0.0;
                        Date date = null;
                        PoiType poiType = null;

                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            if (reader.getAttributeLocalName(i).equals("latitude")) {
                                latitude = Double.parseDouble(reader.getAttributeValue(i));
                            } else if (reader.getAttributeLocalName(i).equals("longitude")) {
                                longitude = Double.parseDouble(reader.getAttributeValue(i));
                            } else if (reader.getAttributeLocalName(i).equals("date")) {
                                date = dateFormat.parse(reader.getAttributeValue(i));
                            } else if (reader.getAttributeLocalName(i).equals("type")) {
                                poiType = Enum.valueOf(PoiType.class, reader.getAttributeValue(i));
                            }
                        }

                        // Woot, the beautiful non understandable error message.
                        // Can be translated: You're fucked :)
                        if (poiType == null) {
                            throw new Exception("Type undefined");
                        }
                        if (date == null) {
                            throw new Exception("Date undefined");
                        }

                        // Everything is created.
                        currentPoi = poiType.constructPoi(latitude, longitude, date);
                    } else if (reader.getLocalName().equals("confirmations")) {
                        currentPoi.setConfirmations(Integer.parseInt(reader.getElementText()));
                    } else if (reader.getLocalName().equals("refutations")) {
                        currentPoi.setNbNotSeen(Integer.parseInt(reader.getElementText()));
                    } else if (reader.getLocalName().equals("removedDate")) {
                        currentPoi.setRemovedDate(dateFormat.parse(reader.getElementText()));
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (reader.getLocalName().equals("poi") && currentPoi != null) {
                        if (currentPoi.getRemovedDate() == null) {
                            poiList.getPoints().add(currentPoi);
                        } else {
                            poiList.getRemovedPoints().add(currentPoi);
                        }
                        currentPoi = null;
                    }
                    break;
                //case XMLStreamConstants.CHARACTERS:
            }
        }

        return poiList;
    }
}
