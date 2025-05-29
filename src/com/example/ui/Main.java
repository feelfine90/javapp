package com.example.ui;

import com.example.dao.PatientDAOImpl;
import com.example.model.Patient;
import com.example.service.PatientService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PatientDAOImpl patientDAO = new PatientDAOImpl();
        PatientService patientService = new PatientService(patientDAO);
        Scanner scanner = new Scanner(System.in);

        Runtime.getRuntime().addShutdownHook(new Thread(patientDAO::closeConnection));

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
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Patient ID: ");
                    long id;
                    try {
                        id = scanner.nextLong();
                        scanner.nextLine(); // Consume newline
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid ID format.");
                        scanner.nextLine(); // Consume the invalid input
                        continue;
                    }
                    System.out.print("Enter Patient Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Patient Age: ");
                    int age;
                    try {
                        age = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid Age format.");
                        scanner.nextLine(); // Consume the invalid input
                        continue;
                    }
                    System.out.print("Enter Medical History: ");
                    String medicalHistory = scanner.nextLine();
                    patientService.addPatient(new Patient(id, name, age, medicalHistory));
                    System.out.println("Patient added successfully.");
                    break;
                case 2:
                    System.out.print("Enter Patient ID to view: ");
                    long viewId;
                    try {
                        viewId = scanner.nextLong();
                        scanner.nextLine(); // Consume newline
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid ID format.");
                        scanner.nextLine(); // Consume the invalid input
                        continue;
                    }
                    Patient patient = patientService.getPatientById(viewId);
                    if (patient != null) {
                        System.out.println("Patient Details: " + patient.getId() + ", " + patient.getName() + ", " + patient.getAge() + ", " + patient.getMedicalHistory());
                    } else {
                        System.out.println("Patient not found.");
                    }
                    break;
                case 3:
                    List<Patient> patients = patientService.getAllPatients();
                    if (patients.isEmpty()) {
                        System.out.println("No patients found.");
                    } else {
                        System.out.println("All Patients:");
                        for (Patient p : patients) {
                            System.out.println(p.getId() + ", " + p.getName() + ", " + p.getAge() + ", " + p.getMedicalHistory());
                        }
                    }
                    break;
                case 4:
                    System.out.print("Enter Patient ID to update: ");
                    long updateId;
                     try {
                        updateId = scanner.nextLong();
                        scanner.nextLine(); // Consume newline
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid ID format.");
                        scanner.nextLine(); // Consume the invalid input
                        continue;
                    }
                    Patient existingPatient = patientService.getPatientById(updateId);
                    if (existingPatient == null) {
                        System.out.println("Patient not found with ID: " + updateId);
                        continue;
                    }
                    System.out.print("Enter new Name (current: " + existingPatient.getName() + "): ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new Age (current: " + existingPatient.getAge() + "): ");
                    int newAge;
                    try {
                        newAge = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid Age format.");
                        scanner.nextLine(); // Consume the invalid input
                        continue;
                    }
                    System.out.print("Enter new Medical History (current: " + existingPatient.getMedicalHistory() + "): ");
                    String newMedicalHistory = scanner.nextLine();
                    patientService.updatePatient(new Patient(updateId, newName, newAge, newMedicalHistory));
                    System.out.println("Patient updated successfully.");
                    break;
                case 5:
                    System.out.print("Enter Patient ID to delete: ");
                    long deleteId;
                    try {
                        deleteId = scanner.nextLong();
                        scanner.nextLine(); // Consume newline
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid ID format.");
                        scanner.nextLine(); // Consume the invalid input
                        continue;
                    }
                    patientService.deletePatient(deleteId);
                    System.out.println("Patient deleted successfully.");
                    break;
                case 6:
                    System.out.println("Exiting application.");
                    scanner.close();
                    // patientDAO.closeConnection(); // Connection is closed by shutdown hook
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
