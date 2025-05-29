package com.example.dao;

import com.example.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatientDAOImplTest {

    private PatientDAOImpl patientDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Using a unique DB for each test run to ensure isolation, though H2 in-mem is usually clean.
        // For PatientDAOImpl, it creates its own connection, so this is more for direct manipulation if needed.
        // The PatientDAOImpl constructor will establish its own connection and setup the table.
        patientDAO = new PatientDAOImpl(); 
    }

    @AfterEach
    void tearDown() throws SQLException {
        // The PatientDAOImpl has a shutdown hook for its connection.
        // If we were managing connection here, we'd close it.
        // For H2 in-memory, DB_CLOSE_DELAY=-1 keeps it alive, 
        // but a new PatientDAOImpl() means a fresh start if the DB name is constant.
        // To ensure total cleanup for external test DBs or more complex scenarios,
        // one might drop the table or close the connection explicitly if DAO provided a method.
        patientDAO.closeConnection(); // Ensure connection is closed after each test
    }

    @Test
    void testAddAndGetPatient() {
        Patient patient = new Patient(1L, "John Doe", 30, "Flu");
        patientDAO.addPatient(patient);

        Patient retrievedPatient = patientDAO.getPatientById(1L);
        assertNotNull(retrievedPatient);
        assertEquals("John Doe", retrievedPatient.getName());
        assertEquals(30, retrievedPatient.getAge());
        assertEquals("Flu", retrievedPatient.getMedicalHistory());
    }

    @Test
    void testGetAllPatients() {
        Patient patient1 = new Patient(1L, "John Doe", 30, "Flu");
        Patient patient2 = new Patient(2L, "Jane Smith", 25, "Cold");
        patientDAO.addPatient(patient1);
        patientDAO.addPatient(patient2);

        List<Patient> patients = patientDAO.getAllPatients();
        assertNotNull(patients);
        assertEquals(2, patients.size());
    }

    @Test
    void testUpdatePatient() {
        Patient patient = new Patient(1L, "John Doe", 30, "Flu");
        patientDAO.addPatient(patient);

        Patient retrievedPatient = patientDAO.getPatientById(1L);
        assertNotNull(retrievedPatient);
        retrievedPatient.setMedicalHistory("Recovered from Flu");
        retrievedPatient.setAge(31);
        patientDAO.updatePatient(retrievedPatient);

        Patient updatedPatient = patientDAO.getPatientById(1L);
        assertNotNull(updatedPatient);
        assertEquals("Recovered from Flu", updatedPatient.getMedicalHistory());
        assertEquals(31, updatedPatient.getAge());
    }

    @Test
    void testDeletePatient() {
        Patient patient = new Patient(1L, "John Doe", 30, "Flu");
        patientDAO.addPatient(patient);

        Patient retrievedPatient = patientDAO.getPatientById(1L);
        assertNotNull(retrievedPatient, "Patient should exist before deletion");

        patientDAO.deletePatient(1L);
        Patient deletedPatient = patientDAO.getPatientById(1L);
        assertNull(deletedPatient, "Patient should not exist after deletion");
    }

     @Test
    void testGetPatientById_NotFound() {
        Patient retrievedPatient = patientDAO.getPatientById(99L); // Assuming 99L does not exist
        assertNull(retrievedPatient);
    }
}
