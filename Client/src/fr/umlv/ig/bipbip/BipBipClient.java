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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.net.InetSocketAddress;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class BipBipClient extends JFrame {

    private static final String TITLE = "BipBip Client";
    private static final long POI_UPDATE_INTERVAL = 60000;
    private static final int width = 550;
    private static final int height = 450;
    private final MapPOIModel mapPOIModel;
    private final JMap map;

    public BipBipClient() throws HeadlessException {
        super(TITLE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Map
        mapPOIModel = new MapPOIModel();
        map = new JMap(mapPOIModel);
        add(map);
        
        // Right toolBar
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        
        
        JButton button1 = new JButton("Radar fixe");
        toolbar.add(button1);
        
        JButton button2 = new JButton("Radar mobile");
        toolbar.add(button2);
        
        JButton button3 = new JButton("Accident");
        toolbar.add(button3);
        
        JButton button4 = new JButton("Divers");
        toolbar.add(button4);
        
        JButton button5 = new JButton("Travaux");
        toolbar.add(button5);
        
        JButton button6 = new JButton("Refresh");
        toolbar.add(button6);
        
        add(toolbar, BorderLayout.EAST);

        setVisible(true);
    }

    public static void main(String[] args) {
        BipBipClient frame = new BipBipClient();
        new Thread(new MapPOIModelUpdater(frame.mapPOIModel,
                frame.map, new InetSocketAddress(args[0],
                Integer.parseInt(args[1])),
                POI_UPDATE_INTERVAL)).start();
    }
}
