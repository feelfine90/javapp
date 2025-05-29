package com.example.ui;

import com.example.dao.PatientDAOImpl;
import com.example.service.PatientService;
import com.example.exception.DataAccessException;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        // Initialize DAO and Service
        PatientDAOImpl patientDAO;
        try {
            patientDAO = new PatientDAOImpl();
            
            // Setup shutdown hook for DB connection closure, similar to CLI version
            // This ensures the connection is closed when the Swing app window is closed (EXIT_ON_CLOSE)
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (patientDAO != null) {
                        patientDAO.closeConnection();
                        System.out.println("Database connection closed via shutdown hook.");
                    }
                } catch (DataAccessException e) {
                    // Log to console or a file, as UI might be gone
                    System.err.println("Error closing database connection during shutdown: " + e.getMessage());
                    e.printStackTrace();
                }
            }));

        } catch (DataAccessException e) {
            // If DAO fails to initialize, it's a critical error.
            // Show error in a JOptionPane as GUI hasn't started yet.
            String errorMessage = "Critical Error: Failed to initialize database access.\n" +
                                  e.getMessage() +
                                  "\nThe application will now exit.";
            JOptionPane.showMessageDialog(null, errorMessage, "Initialization Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Log for developer
            System.exit(1);      // Exit application
            return;              // Necessary for compiler if System.exit wasn't there.
        }
        
        PatientService patientService = new PatientService(patientDAO);

        // Launch the Swing application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Pass the initialized PatientService to the Swing application
                PatientManagementApp appFrame = new PatientManagementApp(patientService);
                appFrame.setVisible(true);
            }
        });
    }
}
