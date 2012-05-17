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

import fr.umlv.ig.bipbip.BipbipServer;
import fr.umlv.ig.bipbip.EventType;
import fr.umlv.ig.bipbip.poi.POI;
import fr.umlv.ig.bipbip.server.POIComparator;
import fr.umlv.ig.bipbip.server.POIEvent;
import fr.umlv.ig.bipbip.server.POIListener;
import fr.umlv.ig.bipbip.poi.defined.RadarFixe;
import fr.umlv.ig.bipbip.poi.swing.JPOI;
import fr.umlv.ig.bipbip.server.ServerPOIList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Server window.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class ServerJFrame extends JFrame {

    // Connected objects
    private final BipbipServer server;
    private final Logger clientCommandLogger;
    private ServerPOIList serverPOIList;
    private final DefaultListModel<LogRecord> clientCommandLogList;
    private final POITableModel poiTableModel;
    // For the JMapPanel
    private final HashMap<POI, JPOI> POItoJPOI = new HashMap<POI, JPOI>();
    //
    // GUI objects.
    //
    private JMenuBar menuBar;
    // File menu.
    private JMenu menuFile;
    private JMenuItem menuFileOpen;
    private JMenuItem menuFileSave;
    private JMenuItem menuFileQuit;
    // Connection menu.
    private JMenu menuConnection;
    private JMenuItem menuConnectionStart;
    private JMenuItem menuConnectionStop;
    // Help menu.
    private JMenu menuHelp;
    private JMenuItem menuHelpAbout;
    // Main horizontal split pane.
    private JSplitPane horizontalSplitPane;
    // -> Bottom part of the pane.
    // Log list.
    private JScrollPane clientCommandScrollPane;
    private JList clientCommandLog;
    // -> Top part of the pane.
    // Main vertical split pane.
    private JSplitPane verticalSplitPane;
    // - -> Left part
    private JMapViewer map;
    // - -> Right part
    private JPanel poiPanel;
    // Toolbar for managing POI.
    private JToolBar poiToolbar;
    private JButton poiAdd;
    private JButton poiEdit;
    private JButton poiRemove;
    // Table of POI.
    private JScrollPane poiTableScrollPane;
    private JTable poiTable;

    /**
     * Creates a new server frame.
     *
     * @param server Server to connect to.
     * @param clientCommandLogger Logger that log client commands.
     * @param serverPOIList List of points of interests to connect to.
     */
    public ServerJFrame(BipbipServer server, Logger clientCommandLogger, ServerPOIList serverPOIList) {
        super("Bipbip server");

        // Linking with objects. TODO: (Maybe put that outside within a connect method?)
        this.server = server;
        this.clientCommandLogger = clientCommandLogger;
        this.serverPOIList = serverPOIList;

        // Registering the log.
        clientCommandLogList = new DefaultListModel<LogRecord>();
        clientCommandLogger.addHandler(new CommandLogHandler());

        // Registering the pois table.
        poiTableModel = new POITableModel();
        serverPOIList.addPOIListener(new POIEventHandler());

        // Creation of the GUI.

        // Menu.
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // Menu file.
        menuFile = new JMenu("File");
        menuBar.add(menuFile);

        menuFileOpen = new JMenuItem("Open a database");
        menuFile.add(menuFileOpen);

        menuFileSave = new JMenuItem("Save the database");
        menuFile.add(menuFileSave);

        // Separator.
        menuFile.add(new JSeparator());

        menuFileQuit = new JMenuItem("Quit");
        menuFile.add(menuFileQuit);

        // Menu connection
        menuConnection = new JMenu("Connection");
        menuBar.add(menuConnection);

        menuConnectionStart = new JMenuItem("Start server");
        menuConnection.add(menuConnectionStart);

        menuConnectionStop = new JMenuItem("Stop server");
        menuConnection.add(menuConnectionStop);

        // Menu help
        menuHelp = new JMenu("Help");
        menuBar.add(menuHelp);

        menuHelpAbout = new JMenuItem("About BipBip server...");
        menuHelp.add(menuHelpAbout);

        // Horizontal Split pane.
        horizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.add(horizontalSplitPane);

        // Log
        clientCommandLog = new JList(clientCommandLogList);
        clientCommandScrollPane = new JScrollPane(clientCommandLog);
        horizontalSplitPane.setBottomComponent(clientCommandScrollPane);

        clientCommandLog.setCellRenderer(new CommandLogRenderer());

        // Vertical split plane.
        verticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        horizontalSplitPane.setTopComponent(verticalSplitPane);

        // Map
        map = new JMapViewer();

        // Filling the map with the already defined POI.
        for (POI poi : serverPOIList.getPoints()) {
            // Storing the JPOI
            JPOI jpoi = new JPOI(poi);
            POItoJPOI.put(poi, jpoi);

            // Displaying the marker.
            map.addMapMarker(jpoi);
        }

        verticalSplitPane.setLeftComponent(map);

        // List of POI.
        poiPanel = new JPanel(new BorderLayout());
        verticalSplitPane.setRightComponent(poiPanel);
        // Toolbar
        poiToolbar = new JToolBar();
        poiToolbar.setFloatable(false);
        poiPanel.add(poiToolbar, BorderLayout.PAGE_START);

        poiAdd = new JButton("Add POI");
        poiToolbar.add(poiAdd);

        poiEdit = new JButton("Edit POI");
        poiToolbar.add(poiEdit);

        poiRemove = new JButton("Remove POI");
        poiToolbar.add(poiRemove);

        // Table
        poiTable = new JTable(poiTableModel);
        poiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        poiTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListElementSelected();
            }
        });

        poiTableScrollPane = new JScrollPane(poiTable);
        poiPanel.add(poiTableScrollPane, BorderLayout.CENTER);

        RowSorter<POITableModel> sorter = new TableRowSorter<POITableModel>(poiTableModel);
        sorter.toggleSortOrder(0);
        poiTable.setRowSorter(sorter);

        // Over.
    }

    /**
     * Handles when a line is selected in the table.
     *
     * @param index The line selected in the table.
     */
    private void ListElementSelected() {
        int index = poiTable.getSelectedRow();
        if (index < 0) {
            return;
        }
        index = poiTable.convertRowIndexToModel(index); // Converting the value from the sorted display to the model one.

        POI poi = (POI) serverPOIList.getPoints().toArray()[index];
        map.setDisplayPositionByLatLon(poi.getY(), poi.getX(), 4);
    }

    /**
     * Handles the updates of the poi collection.
     */
    private class POIEventHandler implements POIListener {

        @Override
        public void poiAdded(POIEvent e) {
            poiTableModel.fireTableDataChanged();

            // Storing the JPOI
            JPOI jpoi = new JPOI(e.getPoi());
            POItoJPOI.put(e.getPoi(), jpoi);

            // Displaying the marker.
            map.addMapMarker(jpoi);
        }

        @Override
        public void poiUpdated(POIEvent e) {
            poiTableModel.fireTableDataChanged();
        }

        @Override
        public void poiRemoved(POIEvent e) {
            poiTableModel.fireTableDataChanged();

            // Removing the marker.
            map.removeMapMarker(POItoJPOI.get(e.getPoi()));
        }
    }

    /**
     * Table model of the list of active POI.
     */
    private class POITableModel extends AbstractTableModel {

        private final String[] columnNames = {"Date", "Type", "+", "-", "X", "Y"};
        private final Class[] columnClass = {Date.class, EventType.class, int.class, int.class, double.class, double.class};

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
            return serverPOIList.getPoints().size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            POI poi = (POI) serverPOIList.getPoints().toArray()[rowIndex];
            switch (columnIndex) {
                case 0:
                    return poi.getDate();
                case 1:
                    return poi.getType();
                case 2:
                    return poi.getConfirmations();
                case 3:
                    return poi.getRefusals();
                case 4:
                    return poi.getX();
                case 5:
                    return poi.getY();
                default:
                    throw new UnsupportedOperationException("Unknown column");
            }
        }
    }

    /**
     * Handles the log event operations.
     */
    private class CommandLogHandler extends Handler {

        @Override
        public void publish(LogRecord record) {
            clientCommandLogList.addElement(record);
        }

        @Override
        public void flush() {
            clientCommandLogList.clear();
        }

        @Override
        public void close() throws SecurityException {
            // Nothing to do.
        }
    }

    /**
     * Renders the logs.
     */
    private class CommandLogRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // Getting the log.
            LogRecord logRecord = (LogRecord) value;

            Component result = super.getListCellRendererComponent(list, logRecord.getMessage(), index, isSelected, cellHasFocus);
            
            if (logRecord.getLevel().intValue() == Level.WARNING.intValue())
                result.setForeground(Color.ORANGE);
            else if (logRecord.getLevel().intValue() == Level.SEVERE.intValue())
                result.setForeground(Color.red);

            return result;
        }
    }
}
