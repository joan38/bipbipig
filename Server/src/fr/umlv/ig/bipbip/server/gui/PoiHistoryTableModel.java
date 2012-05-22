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
import java.util.ArrayList;
import java.util.Date;
import java.util.SortedSet;
import javax.swing.table.AbstractTableModel;

/**
 * Table model of the list of all Poi.
 * 
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiHistoryTableModel extends AbstractTableModel implements PoiTableModel {
    
    private final String[] columnNames = {"Date", "Removed Date", "Type", "+", "-", "X", "Y"};
    private final Class[] columnClass = {Date.class, Date.class, PoiType.class, Integer.class, Integer.class, Double.class, Double.class};
    private final PoiList poiList;
    private Date currentFilterDate;
    private Date firstPoiDate;
    private ArrayList<Poi> poiData = new ArrayList<Poi>();
    
    /**
     * Creates a new table model.
     *
     * @param poiList List of Poi that this model will display.
     */
    public PoiHistoryTableModel(PoiList poiList) {
        this.poiList = poiList;
        this.poiList.addPoiListener(new PoiEventHandler()); // Listening the changes in the poilist.
        
        this.currentFilterDate = new Date();
        firstPoiDate = new Date();
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
        return poiData.size();
    }

    public PoiList getPoiList() {
        return poiList;
    }

    /**
     * Set the current filter date.
     * 
     * @param currentFilterDate The current filter date.
     */
    public void setCurrentFilterDate(Date currentFilterDate) {
        this.currentFilterDate = currentFilterDate;
        firstPoiDate = new Date();
        poiData = poiList.getAllPoints(currentFilterDate, firstPoiDate);
        
        this.fireTableDataChanged();
    }

    /**
     * Get the current filter date.
     */
    public Date getCurrentFilterDate() {
        return currentFilterDate;
    }

    /**
     * Gets the date of the first element.
     */
    public Date getFirstPoiDate() {
        return firstPoiDate;
    }
    
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Poi poi = (Poi) poiData.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return poi.getDate();
            case 1:
                return poi.getRemovedDate();
            case 2:
                return poi.getType();
            case 3:
                return poi.getConfirmations();
            case 4:
                return poi.getRefutations();
            case 5:
                return poi.getLat();
            case 6:
                return poi.getLon();
            default:
                throw new UnsupportedOperationException("Unknown column");
        }
    }

    @Override
    public ArrayList<Poi> getPoints() {
        return poiData;
    }

    private class PoiEventHandler implements PoiListener {

        @Override
        public void poiAdded(PoiEvent e) {
            PoiHistoryTableModel.this.fireTableDataChanged();
        }

        @Override
        public void poiUpdated(PoiEvent e) {
            PoiHistoryTableModel.this.fireTableDataChanged();
        }

        @Override
        public void poiRemoved(PoiEvent e) {
            PoiHistoryTableModel.this.fireTableDataChanged();
        }
    }
}
