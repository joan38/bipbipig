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

import fr.umlv.ig.bipbip.images.ImageFactory;
import fr.umlv.ig.bipbip.poi.MapPOIModel;
import fr.umlv.ig.bipbip.poi.swing.JPOI;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class BipBipClient extends JFrame {

    private static final String TITLE = "BipBip Client";
    private static final long POI_UPDATE_INTERVAL = 60000;
    private static final int width = 550;
    private static final int height = 450;

    public static void main(String[] args) {
        JFrame frame = new JFrame(TITLE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new LayeredLayoutManager());
        frame.setContentPane(layeredPane);

        // Map
        MapPOIModel mapPOIModel = new MapPOIModel();
        final JMap map = new JMap(mapPOIModel);
        map.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON3) {
                    Point mousePosition = event.getPoint();
                    List<MapMarker> mapMarkers = map.getMapMarkerList();
                    for (MapMarker mapMarker : mapMarkers) {
                        JPOI jpoi = (JPOI) mapMarker;
                        if (jpoi.getIconArea().contains(mousePosition)) {
                            PopupTools.getPOIPopupMenu(jpoi.getPoi().getType()).show(map, mousePosition.x, mousePosition.y);
                            return;
                        }
                    }

                    PopupTools.getMapPopupMenu().show(map, mousePosition.x, mousePosition.y);
                }
            }
        });
        final MapPOIModelUpdater mapPOIModelUpdater = new MapPOIModelUpdater(mapPOIModel,
                map,
                new InetSocketAddress(args[0], Integer.parseInt(args[1])),
                POI_UPDATE_INTERVAL);
        new Thread(mapPOIModelUpdater).start();
        layeredPane.add(map);

        // Refresh button
        JLabel refresh = new JLabel(ImageFactory.getImage("refresh.png"));
        refresh.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                try {
                    mapPOIModelUpdater.update();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolPanel.setOpaque(false);
        toolPanel.add(refresh);
        layeredPane.add(toolPanel, JLayeredPane.PALETTE_LAYER);

        frame.setVisible(true);
    }
}
