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
import fr.umlv.ig.bipbip.poi.PoiEvent;
import fr.umlv.ig.bipbip.poi.PoiListener;
import fr.umlv.ig.bipbip.poi.PoiType;
import fr.umlv.ig.bipbip.server.data.PoiList;
import java.util.Date;
import javax.swing.table.AbstractTableModel;

/**
 * Table model of the list of active POI.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Date", "Type", "+", "-", "X", "Y"};
    private final Class[] columnClass = {Date.class, PoiType.class, Integer.class, Integer.class, Double.class, Double.class};
    private final PoiList poiList;
    private final boolean displayAllPoints;

    public PoiList getPoiList() {
        return poiList;
    }

    /**
     * Creates a new table model.
     *
     * @param poiList List of Poi that this model will display.
     * @param displayAllPoints Display all points including the removed ones.
     */
    public PoiTableModel(PoiList poiList, boolean displayAllPoints) {
        this.poiList = poiList;
        this.displayAllPoints = displayAllPoints;
        this.poiList.addPOIListener(new POIEventHandler()); // Listening the changes in the poilist.
    }

    @Override
    public Class getColumnClass(int column) {
        return columnClass[column];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        if (displayAllPoints) {
            return poiList.getAllPoints().size();
        } else {
            return poiList.getPoints().size();
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Poi poi;
        if (displayAllPoints) {
            poi = (Poi) poiList.getAllPoints().values().toArray()[rowIndex];
        } else {
            poi = (Poi) poiList.getPoints().toArray()[rowIndex];
        }

        switch (columnIndex) {
            case 0:
                return poi.getDate();
            case 1:
                return poi.getType();
            case 2:
                return poi.getConfirmations();
            case 3:
                return poi.getRefutations();
            case 4:
                return poi.getLat();
            case 5:
                return poi.getLon();
            default:
                throw new UnsupportedOperationException("Unknown column");
        }
    }
    
    private class POIEventHandler implements PoiListener {

        @Override
        public void poiAdded(PoiEvent e) {
            PoiTableModel.this.fireTableDataChanged();
        }

        @Override
        public void poiUpdated(PoiEvent e) {
            PoiTableModel.this.fireTableDataChanged();
        }

        @Override
        public void poiRemoved(PoiEvent e) {
            PoiTableModel.this.fireTableDataChanged();
        }
        
    }
}
