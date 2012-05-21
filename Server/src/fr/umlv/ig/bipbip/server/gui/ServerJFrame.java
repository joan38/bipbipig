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
import fr.umlv.ig.bipbip.poi.swing.JPoi;
import fr.umlv.ig.bipbip.server.Server;
import fr.umlv.ig.bipbip.server.data.ServerPoiList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.xml.stream.XMLStreamException;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Server window.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class ServerJFrame extends JFrame {

    // Connected objects
    private final Server server;
    private final ArrayList<Logger> loggersToDisplay;
    private ServerPoiList serverPOIList;
    private final LogListModel clientCommandLogList;
    private final POITableModel poiTableModel;
    // For the JMapPanel
    private final HashMap<Poi, JPoi> poiToJPoi = new HashMap<Poi, JPoi>();
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
    // File chooser
    JFileChooser databaseFileChooser;

    /**
     * Creates a new server frame.
     *
     * @param server Server to connect to.
     * @param loggersToDisplay Collections of loggers to display inside this
     * window.
     * @param serverPOIList List of points of interests to connect to.
     */
    public ServerJFrame(Server server, ArrayList<Logger> loggersToDisplay, ServerPoiList serverPOIList) {
        super("Bipbip server - Port: " + server.getPort());
        Objects.requireNonNull(server);
        Objects.requireNonNull(loggersToDisplay);
        Objects.requireNonNull(serverPOIList);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.loggersToDisplay = loggersToDisplay;

        // Linking with objects.
        this.server = server;
        this.serverPOIList = serverPOIList;

        // Registering the loggers.
        clientCommandLogList = new LogListModel();

        CommandLogHandler guiLogHandler = new CommandLogHandler();
        for (Logger logger : loggersToDisplay) {
            logger.addHandler(guiLogHandler);
        }

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
        menuFileOpen.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuFileOpen.setMnemonic('o');
        menuFile.add(menuFileOpen);

        menuFileSave = new JMenuItem("Save the database");
        menuFileSave.setAccelerator(KeyStroke.getKeyStroke('S', java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuFileSave.setMnemonic('s');
        menuFile.add(menuFileSave);

        // Separator.
        menuFile.add(new JSeparator());

        menuFileQuit = new JMenuItem("Quit");
        menuFileQuit.setAccelerator(KeyStroke.getKeyStroke('Q', java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuFileQuit.setMnemonic('q');
        menuFile.add(menuFileQuit);

        // Menu connection
        menuConnection = new JMenu("Connection");
        menuBar.add(menuConnection);

        menuConnectionStart = new JMenuItem("Start server");
        menuConnectionStart.setMnemonic('s');
        menuConnection.add(menuConnectionStart);

        menuConnectionStop = new JMenuItem("Stop server");
        menuConnectionStop.setMnemonic('o');
        menuConnection.add(menuConnectionStop);

        // Menu help
        menuHelp = new JMenu("Help");
        menuBar.add(menuHelp);

        menuHelpAbout = new JMenuItem("About BipBip server...");
        menuHelpAbout.setAccelerator(KeyStroke.getKeyStroke("F1"));
        menuHelp.add(menuHelpAbout);

        // Horizontal Split pane.
        horizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.add(horizontalSplitPane);

        // Log
        clientCommandLog = new JList(clientCommandLogList);
        clientCommandLog.setPrototypeCellValue(new LogRecord(Level.OFF, "1"));
        clientCommandScrollPane = new JScrollPane(clientCommandLog);
        horizontalSplitPane.setBottomComponent(clientCommandScrollPane);

        clientCommandLog.setCellRenderer(new CommandLogRenderer());

        // Vertical split plane.
        verticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        horizontalSplitPane.setTopComponent(verticalSplitPane);

        // Map
        map = new JMapViewer();

        // Filling the map with the already defined POI.
        for (Poi poi : serverPOIList.getPoints()) {
            // Storing the JPOI
            JPoi jpoi = new JPoi(poi);
            poiToJPoi.put(poi, jpoi);

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

        // File chooser
        databaseFileChooser = new JFileChooser();
        databaseFileChooser.setFileFilter(new FileNameExtensionFilter("XML file", "xml"));

        // Table
        poiTable = new JTable(poiTableModel);
        poiTable.setFocusable(false);
        poiTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        poiTable.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, DateFormat.getDateTimeInstance().format((Date) value), isSelected, hasFocus, row, column);
            }
        });
        poiTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                listElementSelected();
            }
        });
        poiTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedPoi();
                }
            }
        });

        poiTableScrollPane = new JScrollPane(poiTable);
        poiPanel.add(poiTableScrollPane, BorderLayout.CENTER);

        RowSorter<POITableModel> sorter = new TableRowSorter<POITableModel>(poiTableModel);
        sorter.toggleSortOrder(0);
        poiTable.setRowSorter(sorter);

        // Registering handlers.
        poiAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createNewPoi();
            }
        });

        poiEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedPoi();
            }
        });

        poiRemove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedPoi();
            }
        });

        menuConnection.addMenuListener(new MenuListener() {

            @Override
            public void menuSelected(MenuEvent e) {
                if (ServerJFrame.this.server.getConnected()) {
                    menuConnectionStart.setEnabled(false);
                    menuConnectionStop.setEnabled(true);
                } else {
                    menuConnectionStart.setEnabled(true);
                    menuConnectionStop.setEnabled(false);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        menuConnectionStart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ServerJFrame.this.server.serve();
            }
        });

        menuConnectionStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ServerJFrame.this.server.disconnect();
            }
        });

        // Exit?
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (askBeforeClose()) {
                    // Stopping the server.
                    System.exit(0);
                }
            }
        });

        menuFileQuit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (askBeforeClose()) {
                    // Stopping the server.
                    System.exit(0);
                }
            }
        });

        menuHelpAbout.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ServerJFrame.this, "BipBipServer - BipBip Project\n\nDamien Girard and Joan Goyeau");
            }
        });

        menuFileOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openDatabase();
            }
        });

        menuFileSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveDatabase();
            }
        });

        listElementSelected();
    }

    /**
     * Asks the user to open a database file.
     */
    private void openDatabase() {
        boolean serverIsRunning = server.getConnected();

        databaseFileChooser.setDialogTitle("Open a database");
        if (databaseFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(databaseFileChooser.getSelectedFile());
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error while opening the database", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If the server is running, telling the user that it has to be stopped
            // in order to load the new database.
            // (No concurrency problem so)
            if (serverIsRunning) {
                if (JOptionPane.showConfirmDialog(this, "You must stop the server in order to load the new database.\n\nDo you want to stop the server and open the new database?", "Open a database", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                    return;
                }

                // Disconnecting.
                server.disconnect();
            }

            try {
                // Opening the file.
                serverPOIList = ServerPoiList.readFromFile(inputStream);
            } catch (XMLStreamException ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error while opening the database", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error while opening the database", JOptionPane.ERROR_MESSAGE);
                return;
            } finally {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                }
            }

            // Database loaded. Reseting the GUI.
            poiToJPoi.clear();
            map.getMapMarkerList().clear();

            // Registering the new list. (MVC)
            serverPOIList.addPOIListener(new POIEventHandler());

            // Filling the map with the new defined POI.
            for (Poi poi : serverPOIList.getPoints()) {
                // Storing the JPOI
                JPoi jpoi = new JPoi(poi);
                poiToJPoi.put(poi, jpoi);

                // Displaying the marker.
                map.addMapMarker(jpoi);
            }

            // Refreshing the JTables
            poiTableModel.fireTableStructureChanged();

            poiTable.revalidate();
            poiTable.repaint();
            map.repaint();

            if (serverIsRunning) {
                if (JOptionPane.showConfirmDialog(this, "Database loaded.\n\nDo you want to restart the server?", "Open a database", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    server.serve();
                }
            }
        }
    }

    /**
     * Asks the user to save the database to a file.
     *
     * @return false If the user canceled the save operation of if there is an
     * exception.
     */
    private boolean saveDatabase() {
        databaseFileChooser.setDialogTitle("Save the database");
        if (databaseFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Handling the extensions.
            File file = databaseFileChooser.getSelectedFile();
            if (file.toString().endsWith(".xml") == false) {
                file = new File(file.getPath() + ".xml");
            }

            // Opening the file
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error while saving the database", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            try {
                serverPOIList.saveToFile(outputStream);
                JOptionPane.showMessageDialog(this, "Database successfully saved\n\nPath: " + file.toString());
            } catch (XMLStreamException ex) {
                JOptionPane.showMessageDialog(this, ex, "Error while saving the database", JOptionPane.ERROR_MESSAGE);
                return false;
            } finally {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Asks the user if he really want to quit the application.
     *
     * @return True if he really want, false otherwise.
     */
    private boolean askBeforeClose() {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to close the server?", "Quit BipBipServer", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            int res = JOptionPane.showConfirmDialog(this, "Do you want to save the database?");
            switch (res) {
                case JOptionPane.YES_OPTION:
                    return saveDatabase();
                case JOptionPane.CANCEL_OPTION:
                    return false;
                default:
                    return true;
            }
        }
        return false;
    }

    /**
     * Displays the dialog to create a new POI.
     */
    private void createNewPoi() {
        PoiEditJFrame frame = new PoiEditJFrame(this, serverPOIList, 0.0, 0.0);
        frame.setVisible(true);
    }

    /**
     * Edits the selected POI in the JTable.
     */
    private void editSelectedPoi() {
        if (poiTable.getSelectedRowCount() != 1) {
            return;
        }

        // Getting the selected row;
        int index = poiTable.getSelectedRow();
        if (index < 0) {
            return;
        }
        index = poiTable.convertRowIndexToModel(index); // Converting the value from the sorted display to the model one.

        Poi poi = (Poi) serverPOIList.getPoints().toArray()[index];

        PoiEditJFrame frame = new PoiEditJFrame(this, serverPOIList, poi);
        frame.setVisible(true);
    }

    /**
     * Asks the user if he really want to delete selected poi, then delete them.
     */
    private void removeSelectedPoi() {
        if (poiTable.getSelectedRowCount() < 1) {
            return;
        }

        // Confirmation dialog.
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete selected point of interest?", "Delete selected POI", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }

        int[] selectedRows = poiTable.getSelectedRows();
        Poi[] poiToDeletes = new Poi[selectedRows.length];
        for (int i = 0; i < selectedRows.length; i++) {
            int index = poiTable.convertRowIndexToModel(selectedRows[i]);
            poiToDeletes[i] = (Poi) serverPOIList.getPoints().toArray()[index];
        }

        for (Poi poi : poiToDeletes) {
            serverPOIList.removePOI(poi);
        }
    }

    /**
     * Handles when a line is selected in the table.
     *
     * @param index The line selected in the table.
     */
    private void listElementSelected() {
        // Multiple row selected or no row selected.
        poiRemove.setEnabled(poiTable.getSelectedRowCount() >= 1);
        if (poiTable.getSelectedRowCount() != 1) {
            poiEdit.setEnabled(false);
            return;
        } else {
            poiEdit.setEnabled(true);
        }

        // Getting the selected row;
        int index = poiTable.getSelectedRow();
        if (index < 0) {
            return;
        }
        index = poiTable.convertRowIndexToModel(index); // Converting the value from the sorted display to the model one.

        Poi poi = (Poi) serverPOIList.getPoints().toArray()[index];
        map.setDisplayPositionByLatLon(poi.getLat(), poi.getLon(), map.getZoom());
    }

    /**
     * Handles the updates of the poi collection.
     */
    private class POIEventHandler implements PoiListener {

        @Override
        public void poiAdded(PoiEvent e) {
            poiTableModel.fireTableDataChanged();

            // Storing the JPOI
            JPoi jpoi = new JPoi(e.getPoi());
            poiToJPoi.put(e.getPoi(), jpoi);

            // Displaying the marker.
            map.addMapMarker(jpoi);
        }

        @Override
        public void poiUpdated(PoiEvent e) {
            poiTableModel.fireTableDataChanged();
        }

        @Override
        public void poiRemoved(PoiEvent e) {
            poiTableModel.fireTableDataChanged();

            // Removing the marker.
            map.removeMapMarker(poiToJPoi.get(e.getPoi()));
        }
    }

    /**
     * Table model of the list of active POI.
     */
    private class POITableModel extends AbstractTableModel {

        private final String[] columnNames = {"Date", "Type", "+", "-", "X", "Y"};
        private final Class[] columnClass = {Date.class, PoiType.class, Integer.class, Integer.class, Double.class, Double.class};

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
            Poi poi = (Poi) serverPOIList.getPoints().toArray()[rowIndex];
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
    }

    /**
     * Handles the log event operations.
     */
    private class CommandLogHandler extends Handler {

        @Override
        public void publish(LogRecord record) {
            clientCommandLogList.add(record);
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

    private class LogListModel extends AbstractListModel<LogRecord> {
        private List<LogRecord> list = Collections.synchronizedList(new ArrayList<LogRecord>());
        
        public void add(LogRecord record) {
            list.add(record);
            fireIntervalAdded(this, list.size() - 1, list.size());
            
            // Scroll to bottom.
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    clientCommandLog.ensureIndexIsVisible(list.size() - 1);
                }
            });
        }
        
        public void clear(){
            int size = list.size();
            list.clear();
            fireIntervalRemoved(this, 0, size);
        }

        @Override
        public int getSize() {
            return list.size();
        }

        @Override
        public LogRecord getElementAt(int index) {
            return list.get(index);
        }
    }

    /**
     * Renders the logs.
     */
    private class CommandLogRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // Getting the log.
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);

            LogRecord logRecord = (LogRecord) value;

            StringBuilder sb = new StringBuilder();
            sb.append(logRecord.getLevel()).append(": ");
            sb.append(dateFormatter.format(new Date(logRecord.getMillis()))).append(" - ");
            sb.append(logRecord.getLoggerName()).append(": ");
            sb.append(logRecord.getMessage());

            Component result = super.getListCellRendererComponent(list, sb.toString(), index, isSelected, cellHasFocus);

            if (logRecord.getLevel().intValue() == Level.WARNING.intValue()) {
                result.setForeground(Color.ORANGE);
            } else if (logRecord.getLevel().intValue() == Level.SEVERE.intValue()) {
                result.setForeground(Color.red);
            }

            return result;
        }
    }
}
