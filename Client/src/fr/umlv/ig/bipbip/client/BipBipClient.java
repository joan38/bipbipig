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

import fr.umlv.ig.bipbip.client.gui.JMap;
import fr.umlv.ig.bipbip.client.gui.LayeredLayoutManager;
import fr.umlv.ig.bipbip.client.gui.ListenerFactory;
import fr.umlv.ig.bipbip.client.model.ServerPoiModel;
import fr.umlv.ig.bipbip.images.ImageFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class BipBipClient {

    private static final String TITLE = "BipBip Client";
    private static final long POI_UPDATE_INTERVAL = 40000;
    private static final int width = 550;
    private static final int height = 450;

    public static void main(String[] args) throws InterruptedException {
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

        // Server Connection handler
        ServerConnection server = new ServerConnection(new InetSocketAddress(args[0], Integer.parseInt(args[1])));

        // Infos
        final JLabel infos = new JLabel(" ");
        infos.setForeground(Color.RED);
        toolPanel.add(infos);
        
        // Map
        final ServerPoiModel model = new ServerPoiModel(server);
        final JMap map = new JMap(model);
        map.addMouseListener(ListenerFactory.getMapInteractionListener(model, infos));
        layeredPane.add(map);

        // Refresh button
        JLabel refresh = new JLabel(ImageFactory.getImage("refresh.png"));
        refresh.addMouseListener(ListenerFactory.getRefreshButtonListener(model, map, infos));
        toolPanel.add(refresh);

        frame.setVisible(true);
        
        // Model updater
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    model.update(map.getPosition());
                    infos.setText(" ");
                } catch (IOException e) {
                    infos.setText(e.getMessage());
                }
            }
        }, 0, POI_UPDATE_INTERVAL);
    }
}
