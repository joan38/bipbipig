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

import fr.umlv.ig.bipbip.images.ImageFactory;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import javax.swing.*;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class BipBipClient {

    private static final String TITLE = "BipBip Client";
    private static final long POI_UPDATE_INTERVAL = 40000;
    private static final int width = 550;
    private static final int height = 450;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Please give the server's host and port in arguments: <host> <port>");
            return;
        }

        // Setup the frame
        JFrame frame = new JFrame(TITLE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new LayeredLayoutManager());
        frame.setContentPane(layeredPane);

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolPanel.setOpaque(false);
        layeredPane.add(toolPanel, JLayeredPane.PALETTE_LAYER);

        // Infos
        JInfo infos = new JInfo();
        toolPanel.add(infos);

        // Server Connection handler
        ServerConnection server = new ServerConnection(new InetSocketAddress(args[0], Integer.parseInt(args[1])));

        // Map
        MapPoiModel mapPOIModel = new MapPoiModel();
        JMap map = new JMap(mapPOIModel);
        map.addMouseListener(ListenerFactory.getMapInteractionListener(server));
        MapPoiModelUpdater mapPOIModelUpdater = new MapPoiModelUpdater(mapPOIModel,
                map,
                server,
                POI_UPDATE_INTERVAL);
        mapPOIModelUpdater.addUpdateListener(infos);
        new Thread(mapPOIModelUpdater).start();
        layeredPane.add(map);

        // Refresh button
        JLabel refresh = new JLabel(ImageFactory.getImage("refresh.png"));
        refresh.addMouseListener(ListenerFactory.getRefreshButtonListener(mapPOIModelUpdater));
        toolPanel.add(refresh);

        frame.setVisible(true);
    }
}
