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
package fr.umlv.ig.bipbip.server.data;

import fr.umlv.ig.bipbip.poi.Poi;
import fr.umlv.ig.bipbip.poi.PoiEvent;
import fr.umlv.ig.bipbip.poi.PoiListener;
import fr.umlv.ig.bipbip.poi.PoiType;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Table model of the list of active POI.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiActiveTableModel extends AbstractTableModel implements PoiTableModel {

    private final String[] columnNames = {"Date", "Type", "+", "-", "X", "Y"};
    private final Class[] columnClass = {Date.class, PoiType.class, Integer.class, Integer.class, Double.class, Double.class};
    private final PoiList poiList;

    public PoiList getPoiList() {
        return poiList;
    }

    /**
     * Creates a new table model.
     *
     * @param poiList List of Poi that this model will display.
     */
    public PoiActiveTableModel(PoiList poiList) {
        this.poiList = poiList;
        this.poiList.addPoiListener(new PoiEventHandler()); // Listening the changes in the poilist.
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
        return poiList.getPois().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Poi poi = (Poi) poiList.getPois().get(rowIndex);

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

    @Override
    public List<Poi> getPoints() {
        return poiList.getPois();
    }

    private class PoiEventHandler implements PoiListener {

        @Override
        public void poiAdded(PoiEvent e) {
            PoiActiveTableModel.this.fireTableDataChanged();
        }

        @Override
        public void poiUpdated(PoiEvent e) {
            PoiActiveTableModel.this.fireTableDataChanged();
        }

        @Override
        public void poiRemoved(PoiEvent e) {
            PoiActiveTableModel.this.fireTableDataChanged();
        }
    }
}
