package com.example.ui;

import com.example.dao.PatientDAOImpl;
import com.example.exception.DataAccessException;
import com.example.exception.PatientNotFoundException;
import com.example.exception.ServiceException;
import com.example.model.Patient;
import com.example.service.PatientService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PatientDAOImpl patientDAO = null;
        try {
            patientDAO = new PatientDAOImpl();
        } catch (DataAccessException e) {
            System.err.println("Critical Error: Could not initialize database: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            // e.printStackTrace(); // For detailed debugging
            System.exit(1); // Exit if DAO can't be initialized
        }

        PatientService patientService = new PatientService(patientDAO);
        Scanner scanner = new Scanner(System.in);

        // Setup shutdown hook to close DB connection
        // Ensure patientDAO is effectively final or use a final reference
        PatientDAOImpl finalPatientDAO = patientDAO;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (finalPatientDAO != null) {
                    finalPatientDAO.closeConnection();
                    System.out.println("Database connection closed.");
                }
            } catch (DataAccessException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }));


        while (true) {
            System.out.println("\nPatient Management System");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patient by ID");
            System.out.println("3. View All Patients");
            System.out.println("4. Update Patient");
            System.out.println("5. Delete Patient");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = -1;
            try {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                } else {
                    System.err.println("Invalid input. Please enter a number.");
                    scanner.next(); // Consume the invalid input
                    continue;
                }
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) { // Should be caught by hasNextInt check generally
                System.err.println("Invalid input type. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                continue;
            }

            try {
                switch (choice) {
                    case 1: // Add Patient
                        System.out.print("Enter Patient ID: ");
                        long id = readLong(scanner);
                        System.out.print("Enter Patient Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Patient Age: ");
                        int age = readInt(scanner);
                        System.out.print("Enter Medical History: ");
                        String medicalHistory = scanner.nextLine();
                        patientService.addPatient(new Patient(id, name, age, medicalHistory));
                        System.out.println("Patient added successfully.");
                        break;
                    case 2: // View Patient by ID
                        System.out.print("Enter Patient ID to view: ");
                        long viewId = readLong(scanner);
                        Patient patient = patientService.getPatientById(viewId);
                        // PatientNotFoundException is handled by the outer catch block
                        System.out.println("Patient Details: ID=" + patient.getId() + ", Name=" + patient.getName() + 
                                           ", Age=" + patient.getAge() + ", History=" + patient.getMedicalHistory());
                        break;
                    case 3: // View All Patients
                        List<Patient> patients = patientService.getAllPatients();
                        if (patients.isEmpty()) {
                            System.out.println("No patients found.");
                        } else {
                            System.out.println("All Patients:");
                            for (Patient p : patients) {
                                System.out.println("ID=" + p.getId() + ", Name=" + p.getName() + 
                                                   ", Age=" + p.getAge() + ", History=" + p.getMedicalHistory());
                            }
                        }
                        break;
                    case 4: // Update Patient
                        System.out.print("Enter Patient ID to update: ");
                        long updateId = readLong(scanner);
                        // Service method getPatientById will throw PatientNotFoundException if not found
                        Patient existingPatient = patientService.getPatientById(updateId); 
                        System.out.println("Updating patient: " + existingPatient.getName());
                        System.out.print("Enter new Name (current: " + existingPatient.getName() + ", press Enter to keep): ");
                        String newName = scanner.nextLine();
                        if (newName.isEmpty()) newName = existingPatient.getName();

                        System.out.print("Enter new Age (current: " + existingPatient.getAge() + ", press Enter to keep): ");
                        String ageStr = scanner.nextLine();
                        int newAge = existingPatient.getAge();
                        if (!ageStr.isEmpty()) newAge = Integer.parseInt(ageStr); // Add try-catch for parseInt

                        System.out.print("Enter new Medical History (current: " + existingPatient.getMedicalHistory() + ", press Enter to keep): ");
                        String newMedicalHistory = scanner.nextLine();
                        if (newMedicalHistory.isEmpty()) newMedicalHistory = existingPatient.getMedicalHistory();
                        
                        patientService.updatePatient(new Patient(updateId, newName, newAge, newMedicalHistory));
                        System.out.println("Patient updated successfully.");
                        break;
                    case 5: // Delete Patient
                        System.out.print("Enter Patient ID to delete: ");
                        long deleteId = readLong(scanner);
                        patientService.deletePatient(deleteId);
                        System.out.println("Patient deleted successfully.");
                        break;
                    case 6:
                        System.out.println("Exiting application.");
                        scanner.close();
                        // Shutdown hook will attempt to close DB connection
                        System.exit(0); // Use System.exit to trigger shutdown hooks
                        return; // Unreachable due to System.exit
                    default:
                        System.err.println("Invalid choice. Please try again.");
                }
            } catch (PatientNotFoundException e) {
                System.err.println("Error: " + e.getMessage());
            } catch (ServiceException e) {
                System.err.println("Service Error: " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("  Underlying cause: " + e.getCause().getMessage());
                }
                // e.printStackTrace(); // For detailed debugging
            } catch (InputMismatchException e) { // Catching specific errors from readLong/readInt
                 System.err.println("Invalid data format for ID or Age. Please enter a valid number.");
                 // scanner.nextLine(); // Already handled in readLong/readInt
            } catch (NumberFormatException e) { // For parsing age in update if not empty
                 System.err.println("Invalid number format for age: " + e.getMessage());
            } catch (Exception e) { // Catch-all for any other unexpected exceptions
                System.err.println("An unexpected error occurred: " + e.getMessage());
                // e.printStackTrace(); // For detailed debugging
            }
        }
    }

    // Helper method to read Long with basic validation
    private static long readLong(Scanner scanner) throws InputMismatchException {
        if (scanner.hasNextLong()) {
            long value = scanner.nextLong();
            scanner.nextLine(); // consume newline
            return value;
        } else {
            String input = scanner.nextLine(); // consume the invalid input
            throw new InputMismatchException("Invalid input: Expected a number (long), but got '" + input + "'");
        }
    }

    // Helper method to read Int with basic validation
    private static int readInt(Scanner scanner) throws InputMismatchException {
         if (scanner.hasNextInt()) {
            int value = scanner.nextInt();
            scanner.nextLine(); // consume newline
            return value;
        } else {
            String input = scanner.nextLine(); // consume the invalid input
            throw new InputMismatchException("Invalid input: Expected a number (int), but got '" + input + "'");
        }
    }
}
