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
import fr.umlv.ig.bipbip.poi.PoiType;
import fr.umlv.ig.bipbip.poi.swing.PoiImageFactory;
import fr.umlv.ig.bipbip.server.data.ServerPoiList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Objects;
import javax.swing.*;

/**
 * Frame used to create a new POI or edit a POI.
 *
 * @author Damien Girard <dgirard@nativesoft.fr>
 */
public class PoiEditJFrame extends JDialog {

    private final ServerPoiList poiList;
    private Poi editedPoi = null;
    
    // GUI.
    //
    // Header
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JLabel typeIcon;
    
    // Configuration components
    private JPanel componentPanel;
    private JLabel dateLabel;
    private JFormattedTextField dateField;
    private JLabel typeLabel;
    private JComboBox<PoiType> typeCombobox;
    private JLabel confirmationLabel;
    private JSpinner confirmationSpinner;
    private JLabel refusedLabel;
    private JSpinner refusedSpinner;
    private JLabel latitudeLabel;
    private JFormattedTextField latitudeField;
    private JLabel longitudeLabel;
    private JFormattedTextField longitudeField;
    // OK/Cancel/Apply
    private JPanel actionPanel;
    private JButton okButton;
    private JButton cancelButton;

    /**
     * Build the shared components between the edition and the creation of a
     * POI.
     */
    private void buildCommonGUI() {
        // Header
        headerPanel = new JPanel(new GridBagLayout());
        this.add(headerPanel, BorderLayout.PAGE_START);

        // Title
        titleLabel = new JLabel("POI Editor");
        titleLabel.setFont(new Font("Dialog", 1, 14));
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.anchor = GridBagConstraints.WEST;
        constraint.weightx = 1.0;
        constraint.insets = new Insets(4, 4, 4, 4);
        headerPanel.add(titleLabel, constraint);

        // Displaying the type icon.
        typeIcon = new JLabel();
        constraint = new GridBagConstraints();
        constraint.anchor = GridBagConstraints.EAST;
        constraint.insets = new Insets(4, 4, 4, 4);
        headerPanel.add(typeIcon, constraint);

        // Values of the component.
        // Init of gridbags.
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.NORTHEAST;
        labelConstraints.insets = new Insets(4, 4, 4, 4);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.anchor = GridBagConstraints.NORTHWEST;
        fieldConstraints.insets = new Insets(4, 4, 4, 4);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;

        // Setting up the panel. (With a scrollbar)
        componentPanel = new JPanel(new GridBagLayout());
        JScrollPane jScrollPane = new JScrollPane(componentPanel);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(jScrollPane, BorderLayout.CENTER);

        // Event type
        labelConstraints.gridy++;
        fieldConstraints.gridy++;

        typeLabel = new JLabel("Type:", SwingConstants.RIGHT);
        componentPanel.add(typeLabel, labelConstraints);

        typeCombobox = new JComboBox<PoiType>(PoiType.values());
        componentPanel.add(typeCombobox, fieldConstraints);

        // Setting the title icon.
        typeIcon.setIcon(PoiImageFactory.getImage((PoiType) typeCombobox.getSelectedItem()));

        // Displaying the Date
        labelConstraints.gridy++;
        fieldConstraints.gridy++;

        dateLabel = new JLabel("Date: ", SwingConstants.RIGHT);
        componentPanel.add(dateLabel, labelConstraints);

        dateField = new JFormattedTextField(DateFormat.getInstance());
        dateField.setValue(new Date());
        componentPanel.add(dateField, fieldConstraints);

        // Number of confirmations
        labelConstraints.gridy++;
        fieldConstraints.gridy++;

        confirmationLabel = new JLabel("Confirmations:", SwingConstants.RIGHT);
        componentPanel.add(confirmationLabel, labelConstraints);

        confirmationSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        GridBagConstraints spinnerConstraints = (GridBagConstraints) fieldConstraints.clone();
        spinnerConstraints.fill = 0;
        componentPanel.add(confirmationSpinner, spinnerConstraints);

        // Number of refusal
        labelConstraints.gridy++;
        fieldConstraints.gridy++;

        refusedLabel = new JLabel("Refused:", SwingConstants.RIGHT);
        componentPanel.add(refusedLabel, labelConstraints);

        refusedSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        spinnerConstraints.gridy = labelConstraints.gridy;
        componentPanel.add(refusedSpinner, spinnerConstraints);

        // Latitude
        labelConstraints.gridy++;
        fieldConstraints.gridy++;

        latitudeLabel = new JLabel("Latitude:");
        componentPanel.add(latitudeLabel, labelConstraints);

        latitudeField = new JFormattedTextField(NumberFormat.getInstance());
        componentPanel.add(latitudeField, fieldConstraints);

        // Longitude
        labelConstraints.gridy++;
        fieldConstraints.gridy++;

        labelConstraints.weighty = 1.0; // For the last element.

        longitudeLabel = new JLabel("Longitude:");
        componentPanel.add(longitudeLabel, labelConstraints);

        longitudeField = new JFormattedTextField(NumberFormat.getInstance());
        componentPanel.add(longitudeField, fieldConstraints);

        // Action panel.
        actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.add(actionPanel, BorderLayout.PAGE_END);

        // GUI Created. Now settings all the handlers.
        //
        // I prefer doing that instead of mix the UI and the code, because
        // the gridBagLayout is a pretty difficult things, so GUI is GUI and 
        // code is code.
        typeCombobox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                typeIcon.setIcon(PoiImageFactory.getImage((PoiType) typeCombobox.getSelectedItem()));
            }
        });
    }

    /**
     * Creates the GUI for inserting a new POI.
     *
     * @param latitude Latitude.
     * @param longitude Longitude.
     */
    private void buildCreateGUI(Double latitude, Double longitude) {
        // Title
        this.setTitle("Create a new POI");
        titleLabel.setText("Create a new POI");

        // Fields
        latitudeField.setValue(latitude);
        longitudeField.setValue(longitude);

        // Create/Cancel button.
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PoiEditJFrame.this.setVisible(false);
            }
        });
        actionPanel.add(cancelButton);

        okButton = new JButton("Create");
        okButton.setDefaultCapable(true);
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createNewPoi();
            }
        });
        actionPanel.add(okButton);
    }

    /**
     * Creates the GUI for editing a POI.
     *
     * @param poi Poi to edit.
     */
    private void buildEditGUI(Poi poi) {
        // Title
        this.setTitle("Create a POI");
        titleLabel.setText("Edit a POI");

        // Fields
        latitudeField.setValue(poi.getLat());
        longitudeField.setValue(poi.getLon());
        confirmationSpinner.setValue(poi.getConfirmations());
        refusedSpinner.setValue(poi.getRefutations());
        dateField.setValue(poi.getDate());
        typeCombobox.setSelectedItem(poi.getType());

        // OK, Cancel, Apply buttons.
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PoiEditJFrame.this.setVisible(false);
            }
        });
        actionPanel.add(cancelButton);

        okButton = new JButton("Save");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editPoi();
            }
        });
        actionPanel.add(okButton);
    }

    /**
     * Creates a new POI.
     *
     * @param poiList List of POI.
     * @param latitude Latitude.
     * @param longitude Longitude.
     */
    public PoiEditJFrame(Frame frame, ServerPoiList poiList, Double latitude, Double longitude) {
        super(frame, true);

        Objects.requireNonNull(poiList);

        this.poiList = poiList;

        // Creation of the frame.
        this.setSize(500, 300);

        buildCommonGUI();
        buildCreateGUI(latitude, longitude);

        setLocationRelativeTo(frame);
    }

    /**
     * Edits a POI.
     *
     * @param poiList List of POI.
     * @param poiToEdit POI to edit.
     */
    public PoiEditJFrame(Frame frame, ServerPoiList poiList, Poi poiToEdit) {
        super(frame, true);

        Objects.requireNonNull(poiList);
        Objects.requireNonNull(poiToEdit);

        this.poiList = poiList;
        this.editedPoi = poiToEdit;

        // Creation of the frame.
        this.setSize(500, 300);

        buildCommonGUI();
        buildEditGUI(poiToEdit);

        setLocationRelativeTo(frame);
    }

    /**
     * Creates a new POI from the data contained inside this dialog.
     *
     * @return A new POI.
     */
    private Poi createPoiFromData() {
        Poi newPoi = ((PoiType) typeCombobox.getSelectedItem()).constructPoi(((Number) latitudeField.getValue()).doubleValue(),
                ((Number) longitudeField.getValue()).doubleValue(),
                (Date) dateField.getValue());
        
        newPoi.setConfirmations((Integer)confirmationSpinner.getValue());
        newPoi.setRefutations((Integer)refusedSpinner.getValue());
        
        return newPoi;
    }

    /**
     * Creates a new POI from the value defined inside this dialog.
     */
    private void createNewPoi() {
        // Adding a new POI to the collection.
        poiList.addPoi(createPoiFromData());

        // Over.
        this.setVisible(false);
    }

    /**
     * Saves the changes applied to the Poi.
     */
    private void editPoi() {
        // Adding a new POI to the collection.
        poiList.updatePoi(editedPoi, createPoiFromData());

        // Over.
        this.setVisible(false);
    }
}
