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
import fr.umlv.ig.bipbip.poi.PoiType;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Communication with the server
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class ServerCommunication {

    private static final int CONNECTION_TIMEOUT = 10000;
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT);
    private final SocketAddress address;
    private SocketChannel channel;

    /**
     * Lazy loading regex of supported POI types
     */
    private static class Regex {

        private static String supportedPoiTypes;

        static {
            StringBuilder sb = new StringBuilder();
            PoiType[] supportedTypes = PoiType.values();
            for (PoiType type : supportedTypes) {
                sb.append(type.name()).append("|");
            }
            sb.deleteCharAt(sb.length() - 1);
            supportedPoiTypes = sb.toString();
        }
    }

    public ServerCommunication(SocketAddress address) {
        Objects.requireNonNull(address);

        this.address = address;
    }

    /**
     * Sumit a new POI to the server
     *
     * A SUBMIT command is supposed to have the following form: SUBMIT <POI
     * type> <latitude> <longitude> <date> <nb confirmation> <date> =
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ
     *
     * @param poi
     * @throws IOException
     */
    public void submit(Poi poi) throws IOException {
        Objects.requireNonNull(poi);

        if (channel == null || !channel.isConnected()) {
            connect();
        }

        String cmd = "SUBMIT " + poi.getType().name() + " " + poi.getLat() + " " + poi.getLon() + " " + dateFormat.format(poi.getDate()) + " " + poi.getConfirmations() + "\n";
        try {
            channel.write(ByteBuffer.wrap(cmd.getBytes()));
        } catch (IOException e) {
            channel.close();
            throw new IOException("Unable to submit the POI to the server", e);
        }
    }

    /**
     * Declare a not seen POI to the server
     *
     * A NOT_SEEN command is supposed to have the following form: NOT_SEEN <POI
     * type> <latitude> <longitude> <date> <date> = yyyy-MM-dd'T'HH:mm:ss.SSSZ
     *
     * @param poi
     * @throws IOException
     */
    public void notSeen(Poi poi) throws IOException {
        Objects.requireNonNull(poi);

        if (channel == null || !channel.isConnected()) {
            connect();
        }

        String cmd = "NOT_SEEN " + poi.getType().name() + " " + poi.getLat() + " " + poi.getLon() + " " + dateFormat.format(poi.getDate()) + "\n";
        try {
            channel.write(ByteBuffer.wrap(cmd.getBytes()));
        } catch (IOException e) {
            channel.close();
            throw new IOException("Unable to report the POI as not seen to the server", e);
        }
    }

    /**
     * Get all POI around the coordinate from the server
     *
     * A INFOS command is supposed to have the following form: INFOS N <line 1>
     * ... <line N>
     *
     * where N is the number of lines of information. Each line is of the form:
     *
     * <line N> = INFO <POI type> <latitude> <longitude> <date> <nb
     * confirmation> <date> = yyyy-MM-dd'T'HH:mm:ss.SSSZ
     *
     * @param coordinate
     * @return
     * @throws IOException
     */
    public ArrayList<Poi> getPois(Coordinate coordinate) throws IOException {
        Objects.requireNonNull(coordinate);

        if (channel == null || !channel.isConnected()) {
            connect();
        }

        String cmd = "GET_INFOS " + coordinate.getLat() + " " + coordinate.getLon() + "\n";
        try {
            channel.write(ByteBuffer.wrap(cmd.getBytes()));
        } catch (IOException e) {
            channel.close();
            throw new IOException("Unable to request POIs from the server", e);
        }

        Scanner scanner = new Scanner(channel);
        if (!scanner.hasNextLine()) {
            channel.close();
            throw new IOException("No response from the server");
        }

        String line = scanner.nextLine();
        if (!line.matches("INFOS \\d+")) {
            channel.close();
            throw new IOException("Invalide answer " + line);
        }

        int nbPoi;
        try {
            nbPoi = Integer.parseInt(line.split(" ")[1]);
        } catch (NumberFormatException e) {
            channel.close();
            throw new IOException("Invalid number of POI: " + line, e);
        }

        ArrayList<Poi> pois = new ArrayList<Poi>();
        for (int i = 0; i < nbPoi; i++) {
            if (!scanner.hasNextLine()) {
                channel.close();
                throw new IOException("Missing INFO answer");
            }
            line = scanner.nextLine();

            if (!line.matches("INFO (" + Regex.supportedPoiTypes + ") \\-?\\d{1,2}.\\d+ \\-?\\d{1,3}.\\d+ \\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d[+-]\\d\\d\\d\\d \\d+")) {
                channel.close();
                throw new IOException("Invalide answer " + line);
            }

            String[] split = line.split(" ");
            PoiType type = PoiType.valueOf(split[1]);
            try {
                pois.add(type.constructPoi(Double.parseDouble(split[2]), Double.parseDouble(split[3]), dateFormat.parse(split[4]), Integer.parseInt(split[5])));
            } catch (NumberFormatException e) {
                channel.close();
                throw new IOException("Invalid answer: " + e.getMessage(), e);
            } catch (ParseException e) {
                channel.close();
                throw new IOException("Invalid date format: " + split[4], e);
            }
        }

        return pois;
    }

    public void connect() throws IOException {
        try {
            channel = SocketChannel.open(address);
            channel.socket().setSoTimeout(CONNECTION_TIMEOUT);
        } catch (IOException e) {
            throw new IOException("Unable to connect to the server", e);
        }
    }
    
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        } else {
            throw new IOException("Channel already closed");
        }
    }
}
