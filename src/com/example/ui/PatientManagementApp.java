package com.example.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ListSelectionModel; // Added import
import javax.swing.event.ListSelectionEvent; // Added import
import javax.swing.event.ListSelectionListener; // Added import
import com.example.service.PatientService;
import com.example.model.Patient;
import com.example.exception.ServiceException;
import com.example.exception.PatientNotFoundException; // Added import


public class PatientManagementApp extends JFrame {

    // Input Fields
    JTextField txtId;
    JTextField txtName;
    JTextField txtAge;
    JTextArea txtMedicalHistory;

    // Buttons
    JButton btnAdd;
    JButton btnUpdate;
    JButton btnDelete;
    JButton btnFindById;
    JButton btnViewAll;
    JButton btnClearFields;

    // Table
    JTable patientTable;

    // Service layer
    private PatientService patientService;

    public PatientManagementApp(PatientService patientService) { // Added service parameter
        super("Patient Management System - Swing UI");
        this.patientService = patientService; // Store service
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout(5, 5)); // Add some spacing between components

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        txtId = new JTextField(20); // Increased width
        inputPanel.add(txtId, gbc);
        gbc.weightx = 0; // Reset

        // Row 1: Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        txtName = new JTextField(20);
        inputPanel.add(txtName, gbc);
        gbc.weightx = 0;

        // Row 2: Age
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        txtAge = new JTextField(5); // Age field doesn't need to be too wide
        inputPanel.add(txtAge, gbc);
        gbc.weightx = 0;

        // Row 3: Medical History
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHEAST; // Align label to top with text area
        inputPanel.add(new JLabel("Medical History:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0; gbc.weighty = 1.0; // Allow text area to grow
        gbc.fill = GridBagConstraints.BOTH; // Allow text area to fill space
        txtMedicalHistory = new JTextArea(5, 20); // 5 rows, 20 columns
        JScrollPane medicalHistoryScrollPane = new JScrollPane(txtMedicalHistory);
        inputPanel.add(medicalHistoryScrollPane, gbc);
        gbc.weightx = 0; gbc.weighty = 0; // Reset
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centered with spacing
        btnAdd = new JButton("Add Patient");
        btnUpdate = new JButton("Update Patient");
        btnDelete = new JButton("Delete Patient");
        btnFindById = new JButton("Find by ID");
        btnViewAll = new JButton("View All / Refresh");
        btnClearFields = new JButton("Clear Fields");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnFindById);
        buttonPanel.add(btnViewAll);
        buttonPanel.add(btnClearFields);
        
        // --- Table Panel (Central Area) ---
        patientTable = new JTable();
        // Default table model will be set later
        JScrollPane tableScrollPane = new JScrollPane(patientTable);

        // --- Assemble Panels ---
        // Create a wrapper panel for input and buttons if needed, or add inputPanel directly to NORTH
        // For this layout, inputPanel to NORTH and buttonPanel to SOUTH is common.
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshPatientTable(); // Initial data load

        setLocationRelativeTo(null); // Center on screen
        // setVisible(true); // Visibility will be controlled by the main application launcher class

        // --- Action Listeners ---
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPatient();
            }
        });

        btnViewAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshPatientTable();
                // Optional: Provide user feedback, though table update is visual.
                // JOptionPane.showMessageDialog(PatientManagementApp.this, "Patient list refreshed.", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnClearFields.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearInputFields();
            }
        });

        btnFindById.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findPatientById();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePatient();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePatient();
            }
        });

        // Table Row Selection Listener
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && patientTable.getSelectedRow() != -1) {
                    int selectedRow = patientTable.getSelectedRow();
                    populateFieldsFromSelectedTableRow(selectedRow);
                }
            }
        });
    }

    private void populateFieldsFromSelectedTableRow(int selectedRow) {
        // Get data from table model
        Object idObj = patientTable.getModel().getValueAt(selectedRow, 0);
        Object nameObj = patientTable.getModel().getValueAt(selectedRow, 1);
        Object ageObj = patientTable.getModel().getValueAt(selectedRow, 2);
        Object historyObj = patientTable.getModel().getValueAt(selectedRow, 3);

        txtId.setText(idObj != null ? idObj.toString() : "");
        txtName.setText(nameObj != null ? nameObj.toString() : "");
        txtAge.setText(ageObj != null ? ageObj.toString() : "");
        txtMedicalHistory.setText(historyObj != null ? historyObj.toString() : "");
        
        txtId.setEditable(false); // Make ID non-editable when a record is loaded from table
    }

    private void clearInputFields() {
        txtId.setText("");
        txtName.setText("");
        txtAge.setText("");
        txtMedicalHistory.setText("");
        txtId.setEditable(true); // Ensure ID field is editable for new entries or finding by ID
    }

    private void findPatientById() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Patient ID to find.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            long id = Long.parseLong(idStr);
            Patient patient = patientService.getPatientById(id); // This throws PatientNotFoundException if not found
            
            // Populate fields with found patient's data
            txtName.setText(patient.getName());
            txtAge.setText(String.valueOf(patient.getAge()));
            txtMedicalHistory.setText(patient.getMedicalHistory());
            // txtId will already have the ID from user input
            txtId.setEditable(false); // Make ID non-editable after a successful find

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Patient ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            clearOtherFieldsOnFindFail();
        } catch (PatientNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Not Found", JOptionPane.WARNING_MESSAGE);
            clearOtherFieldsOnFindFail();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, "Error finding patient: " + ex.getMessage(), "Service Error", JOptionPane.ERROR_MESSAGE);
            clearOtherFieldsOnFindFail();
        }
    }
    
    private void clearOtherFieldsOnFindFail(){
        txtName.setText("");
        txtAge.setText("");
        txtMedicalHistory.setText("");
        txtId.setEditable(true); // Allow user to try a new ID
    }

    private void updatePatient() {
        String idStr = txtId.getText().trim();
        // It's good practice to check if the ID field is editable.
        // If it's editable, it means a patient hasn't been properly loaded for update.
        if (txtId.isEditable() || idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please load a patient first (e.g., using Find by ID or selecting from table) to update.", "No Patient Loaded", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String ageStr = txtAge.getText().trim();
        String medicalHistory = txtMedicalHistory.getText().trim();

        if (name.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Age are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long id;
        int age;
        try {
            id = Long.parseLong(idStr); // ID should be valid as it was loaded and made non-editable
            age = Integer.parseInt(ageStr);
            if (age <= 0) {
                JOptionPane.showMessageDialog(this, "Age must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            // This should ideally not happen for ID if it's loaded and non-editable, but good for age.
            JOptionPane.showMessageDialog(this, "Age must be a valid number. ID should be pre-loaded.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Patient patientToUpdate = new Patient(id, name, age, medicalHistory);

        try {
            patientService.updatePatient(patientToUpdate);
            JOptionPane.showMessageDialog(this, "Patient updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshPatientTable();
            // After successful update, keep the updated patient info in fields, ID remains non-editable.
            // clearInputFields(); // Don't clear, so user sees the updated data. txtId would become editable.
        } catch (PatientNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Error - Not Found", JOptionPane.ERROR_MESSAGE);
            // If patient somehow was deleted between load and update attempt.
            clearInputFields(); // Clear fields and make ID editable again.
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, "Error updating patient: " + ex.getMessage(), "Service Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        String idStr = txtId.getText().trim();
        // Similar to update, check if a patient is actually loaded (ID field non-editable)
        if (txtId.isEditable() || idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please load a patient first (e.g., using Find by ID or selecting from table) to delete.", "No Patient Loaded", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long id;
        try {
            id = Long.parseLong(idStr); // ID should be valid as it was loaded
        } catch (NumberFormatException ex) {
            // This should ideally not happen if patient is loaded and ID field is non-editable
            JOptionPane.showMessageDialog(this, "Patient ID is invalid. Please re-load the patient.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
            clearInputFields(); // Clear and allow re-entry
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete patient with ID " + id + "?\nName: " + txtName.getText(),
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION) {
            return; // User cancelled
        }

        try {
            patientService.deletePatient(id);
            JOptionPane.showMessageDialog(this, "Patient with ID " + id + " deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshPatientTable();
            clearInputFields(); // Clear fields and make ID editable
        } catch (PatientNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Error - Not Found", JOptionPane.ERROR_MESSAGE);
            // Patient might have been deleted by another process/user after loading.
            clearInputFields(); // Clear and make ID editable
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting patient: " + ex.getMessage(), "Service Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPatient() {
        String idStr = txtId.getText().trim();
        String name = txtName.getText().trim();
        String ageStr = txtAge.getText().trim();
        String medicalHistory = txtMedicalHistory.getText().trim();

        if (idStr.isEmpty() || name.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID, Name, and Age are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long id;
        int age;

        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0) {
                JOptionPane.showMessageDialog(this, "Age must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Using the constructor that takes an ID.
        Patient newPatient = new Patient(id, name, age, medicalHistory);

        try {
            patientService.addPatient(newPatient); // This might throw ServiceException if ID already exists
            JOptionPane.showMessageDialog(this, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshPatientTable();
            clearInputFields();
        } catch (ServiceException ex) {
            // Check if the service exception is due to duplicate ID (this depends on service/DAO implementation)
            // For now, a generic message.
            JOptionPane.showMessageDialog(this, "Error adding patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            // ex.printStackTrace(); 
        }
    }

    private void refreshPatientTable() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            String[] columnNames = {"ID", "Name", "Age", "Medical History"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0); // 0 rows initially

            if (patients != null) { // Ensure patients list is not null
                for (Patient patient : patients) {
                    Object[] row = new Object[]{
                        patient.getId(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getMedicalHistory()
                    };
                    model.addRow(row);
                }
            }
            patientTable.setModel(model);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage(),
                                          "Data Load Error", JOptionPane.ERROR_MESSAGE);
            // e.printStackTrace(); // For debugging
        }
    }

    // Main method for quick testing of this Frame (optional, can be removed)
    
    // public static void main(String[] args) {
    //     // This main method is for isolated testing of the UI frame.
    //     // The actual application launch will be from a separate Main class.
    //     com.example.dao.PatientDAOImpl dao = null;
    //     try {
    //         dao = new com.example.dao.PatientDAOImpl();
    //         // Add a dummy patient for testing table display if DB is empty
    //         // dao.addPatient(new Patient(0L, "Test Init", 0, "Init"));
    //     } catch (com.example.exception.DataAccessException e) {
    //         e.printStackTrace();
    //         JOptionPane.showMessageDialog(null, "Failed to connect to database: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
    //         System.exit(1);
    //     }
    //     PatientService service = new PatientService(dao);

    //     SwingUtilities.invokeLater(() -> {
    //         PatientManagementApp app = new PatientManagementApp(service);
    //         app.setVisible(true);
    //     });
    // }
    
}
