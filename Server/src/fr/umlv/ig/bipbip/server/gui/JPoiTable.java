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
import fr.umlv.ig.bipbip.server.data.PoiList;
import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Custom JTable that automatically selects the point in the JMapViewer.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class JPoiTable extends JTable {

    private final JMapViewer map;

    /**
     * Creates the JTable.
     *
     * @param map The map linked with this Poi table.
     * @param poiList The list of Poi displaying in this table.
     * @param dm The PoiTableModel of this JTable.
     */
    public JPoiTable(JMapViewer map, PoiList poiList, PoiTableModel dm) {
        super(dm);
        Objects.requireNonNull(map);
        Objects.requireNonNull(poiList);

        this.map = map;

        this.setFocusable(false);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, DateFormat.getDateTimeInstance().format((Date) value), isSelected, hasFocus, row, column);
            }
        });
        this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                listElementSelected();
            }
        });
    }

    @Override
    public void setModel(TableModel dataModel) {
        if (!(dataModel instanceof PoiTableModel)) {
            throw new IllegalArgumentException("The new model is not a PoiTableModel");
        }

        super.setModel(dataModel);
    }

    /**
     * Handles when a line is selected in the table.
     *
     * @param index The line selected in the table.
     */
    private void listElementSelected() {
        // Getting the selected row;
        int index = this.getSelectedRow();
        if (index < 0) {
            return;
        }
        index = this.convertRowIndexToModel(index); // Converting the value from the sorted display to the model one.

        PoiTableModel model = (PoiTableModel) this.getModel();
        
        Poi poi = (Poi) model.getPoiList().getPoints().toArray()[index];
        map.setDisplayPositionByLatLon(poi.getLat(), poi.getLon(), map.getZoom());
    }
}
