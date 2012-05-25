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
import fr.umlv.ig.bipbip.client.gui.PopupFactory;
import fr.umlv.ig.bipbip.client.model.PoiCommunicationListener;
import fr.umlv.ig.bipbip.client.model.ServerPoiModel;
import fr.umlv.ig.bipbip.images.ImageFactory;
import fr.umlv.ig.bipbip.poi.PoiEvent;
import fr.umlv.ig.bipbip.poi.swing.PoiImageFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import javax.swing.*;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public final class BipBipClient extends JFrame implements PoiCommunicationListener {

    private static final String TITLE = "BipBip Client";
    private static final long POI_UPDATE_INTERVAL = 30000;
    private static final int FRAME_WIDTH = 550;
    private static final int FRAME_HEIGHT = 450;
    private static final int DEFAULT_PORT = 6996;
    private final JLabel infos;
    private final JMap map;

    public static void main(String[] args) throws InterruptedException {
        // Getting server address
        SocketAddress address;
        if (args.length == 2) {
            try {
                address = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number in arguments: " + args[1]);
                return;
            }
        } else if (args.length == 1) {
            address = new InetSocketAddress(args[0], DEFAULT_PORT);
        } else {
            address = PopupFactory.requestConnectionAddress(DEFAULT_PORT);
            if (address == null) {
                return;
            }
        }

        // Init server communication handler
        ServerCommunication server = new ServerCommunication(address);

        // POI Model
        final ServerPoiModel model = new ServerPoiModel(server);

        // Starting query handling asynchronously
        final Thread thread = new Thread(model);
        thread.start();

        // Frame
        final BipBipClient frame = new BipBipClient(model);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                frame.setVisible(false);
                model.stopAutoUpdating();
                thread.interrupt();
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }

    public BipBipClient(final ServerPoiModel model) {
        // Setup the frame
        super(TITLE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2, FRAME_WIDTH, FRAME_HEIGHT);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new LayeredLayoutManager());
        setContentPane(layeredPane);

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolPanel.setOpaque(false);
        layeredPane.add(toolPanel, JLayeredPane.PALETTE_LAYER);

        // Infos label
        infos = new JLabel(" ");
        infos.setForeground(Color.RED);
        toolPanel.add(infos);

        // Map
        map = new JMap(model);
        map.addMouseListener(ListenerFactory.getMapInteractionListener(model));
        layeredPane.add(map);

        // Refresh button
        JLabel refresh = new JLabel(ImageFactory.getImage("refresh.png"));
        refresh.addMouseListener(ListenerFactory.getRefreshButtonListener(model, map, infos));
        toolPanel.add(refresh);

        // Register as a listener of communication event 
        model.addPoiCommunicationListener(this);
        model.startAutoUpdating(map, POI_UPDATE_INTERVAL);
    }

    @Override
    public void poiSubmited(PoiEvent event) {
        infos.setText(" ");
        JOptionPane.showMessageDialog(map,
                "POI submitted, thank you for your contribution",
                "POI submitted",
                JOptionPane.INFORMATION_MESSAGE,
                PoiImageFactory.getImage(event.getPoi().getType()));
    }

    @Override
    public void poiDeclaredAsNotSeen(PoiEvent event) {
        infos.setText(" ");
        JOptionPane.showMessageDialog(map,
                "POI declared as not seen, thank you for your contribution",
                "POI not seen",
                JOptionPane.INFORMATION_MESSAGE,
                ImageFactory.getImage("delete.png"));
    }

    @Override
    public void poisUpdated(PoiEvent event) {
        infos.setText(" ");
    }

    @Override
    public void unableToSubmitPoi(PoiEvent event) {
        infos.setText(event.getException().getMessage());
    }

    @Override
    public void unableToDeclarPoiAsNotSeen(PoiEvent event) {
        infos.setText(event.getException().getMessage());
    }

    @Override
    public void unableToUpdatePois(PoiEvent event) {
        infos.setText(event.getException().getMessage());
    }
}
