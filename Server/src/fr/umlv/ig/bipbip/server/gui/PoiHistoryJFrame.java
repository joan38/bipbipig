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
package fr.umlv.ig.bipbip.server.gui;

import fr.umlv.ig.bipbip.poi.Poi;
import fr.umlv.ig.bipbip.poi.swing.JPoi;
import fr.umlv.ig.bipbip.server.data.PoiHistoryTableModel;
import fr.umlv.ig.bipbip.server.data.PoiList;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DateFormatter;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiHistoryJFrame extends JFrame {

    private static final int FRAME_WIDTH = 770;
    private static final int FRAME_HEIGHT = 430;
    private JPanel topPanel = new JPanel(new GridBagLayout());
    private JFormattedTextField dateField = new JFormattedTextField(new DateFormatter(DateFormat.getDateTimeInstance()));
    private JSlider sliderDate = new JSlider(1, 100);
    private JButton nowButton = new JButton("Now");
    private JButton refreshButton = new JButton("Refresh");
    private JSplitPane splitPane = new JSplitPane();
    private JMapViewer map = new JMapViewer();
    private JPoiTable poiTable;
    private PoiHistoryTableModel model;

    public PoiHistoryJFrame(PoiList poiList) {
        super("Points history navigator");
        Objects.requireNonNull(poiList);

        // Creation of the frame.
        this.add(topPanel, BorderLayout.PAGE_START);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(dateField, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(sliderDate, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        topPanel.add(nowButton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        topPanel.add(refreshButton, gbc);

        this.add(splitPane, BorderLayout.CENTER);

        model = new PoiHistoryTableModel(poiList);
        poiTable = new JPoiTable(map, model);
        JScrollPane jScrollPane = new JScrollPane(poiTable);
        RowSorter<PoiHistoryTableModel> sorter = new TableRowSorter<PoiHistoryTableModel>(model);
        sorter.toggleSortOrder(0);
        poiTable.setRowSorter(sorter);

        splitPane.setLeftComponent(map);
        splitPane.setRightComponent(jScrollPane);

        dateField.setValue(new Date());

        nowButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dateField.setValue(new Date());
                refresh(true);
            }
        });

        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(true);
            }
        });

        sliderDate.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                long maxValue = (new Date()).getTime();
                long minValue = model.getFirstPoiDate().getTime();

                double newDate = minValue + ((maxValue - minValue) * (sliderDate.getValue() / 100.0));

                dateField.setValue(new Date((long) newDate));

                refresh(false);
            }
        });

        refresh(true);

        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    private void refresh(boolean updateSlider) {
        model.setCurrentFilterDate((Date) dateField.getValue());

        // Adding markers.
        map.getMapMarkerList().clear();
        map.repaint();
        for (Poi poi : model.getPoints()) {
            map.addMapMarker(new JPoi(poi));
        }

        if (updateSlider) {
            long minDate = model.getFirstPoiDate().getTime();
            long currentDate = ((Date) dateField.getValue()).getTime();
            long maxDate = new Date().getTime();

            double newPosition = ((currentDate - minDate) * 100) / (maxDate - minDate);

            sliderDate.setValue((int) newPosition);
        }
    }
}
