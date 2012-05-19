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
package fr.umlv.ig.bipbip;

import fr.umlv.ig.bipbip.poi.MapPOIModel;
import fr.umlv.ig.bipbip.poi.POI;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class MapPOIModelUpdater implements Runnable {

    private final Object waiter = new Object();
    private final MapPOIModel model;
    private final InetSocketAddress server;
    private final long updateInterval;
    private final JMapViewer mapViewer;
    private SocketChannel channel;

    /**
     *
     *
     * @param model
     * @param mapViewer
     * @param server
     * @param updateInterval in millisec
     */
    public MapPOIModelUpdater(MapPOIModel model, JMapViewer mapViewer, InetSocketAddress server, long updateInterval) {
        this.model = model;
        this.server = server;
        this.updateInterval = updateInterval;
        this.mapViewer = mapViewer;
    }

    public void update() throws IOException {
        if (channel == null) {
            throw new IOException("The updater has not been connected to the server");
        }
        if (!channel.isConnected()) {
            throw new ClosedChannelException();
        }
        
        synchronized (waiter) {
            waiter.notify();
        }
    }

    @Override
    public void run() {
        try {
            channel = SocketChannel.open();
            channel.connect(server);
            Scanner scanner = new Scanner(channel, NetUtils.getCharset().name());

            while (!Thread.interrupted()) {
                Coordinate coordinate = mapViewer.getPosition();
                ClientCommand.getInfo(channel, coordinate.getLon(), coordinate.getLat());

                if (!scanner.hasNext() || !scanner.next().equals(ServerCommandHandler.INFOS.name())) {
                    throw new IOException("Server did not respond to the GET_INFO query");
                }
                ArrayList<POI> POIs = model.getAllPOI();
                ArrayList<POI> newPOIs = (ArrayList<POI>) ServerCommandHandler.INFOS.handle(channel, scanner);
                for (POI poi : newPOIs) {
                    if (!POIs.contains(poi)) {
                        model.addPOI(poi);
                    }
                }
                for (POI poi : POIs) {
                    if (!newPOIs.contains(poi)) {
                        model.removePOI(poi);
                    }
                }

                try {
                    synchronized (waiter) {
                        waiter.wait(updateInterval);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
