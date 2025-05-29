package com.example.dao;

import com.example.model.Patient;
import com.example.exception.DataAccessException; // Added import
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
// import static org.junit.jupiter.api.Assertions.assertThrows; // Uncomment if specific exception throwing tests are added

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatientDAOImplTest {

    private PatientDAOImpl patientDAO;
    // Connection management is handled within PatientDAOImpl now

    @BeforeEach
    void setUp() throws DataAccessException { // Updated signature
        // PatientDAOImpl constructor now throws DataAccessException
        patientDAO = new PatientDAOImpl(); 
    }

    @AfterEach
    void tearDown() throws DataAccessException { // Updated signature
        // Ensure connection is closed after each test
        if (patientDAO != null) {
            patientDAO.closeConnection(); 
        }
    }

    @Test
    void testAddAndGetPatient() throws DataAccessException { // Updated signature
        Patient patient = new Patient(1L, "John Doe", 30, "Flu");
        patientDAO.addPatient(patient);

        Patient retrievedPatient = patientDAO.getPatientById(1L);
        assertNotNull(retrievedPatient);
        assertEquals("John Doe", retrievedPatient.getName());
        assertEquals(30, retrievedPatient.getAge());
        assertEquals("Flu", retrievedPatient.getMedicalHistory());
    }

    @Test
    void testGetAllPatients() throws DataAccessException { // Updated signature
        Patient patient1 = new Patient(1L, "John Doe", 30, "Flu");
        Patient patient2 = new Patient(2L, "Jane Smith", 25, "Cold");
        patientDAO.addPatient(patient1);
        patientDAO.addPatient(patient2);

        List<Patient> patients = patientDAO.getAllPatients();
        assertNotNull(patients);
        assertEquals(2, patients.size());
    }

    @Test
    void testUpdatePatient() throws DataAccessException { // Updated signature
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
    void testDeletePatient() throws DataAccessException { // Updated signature
        Patient patient = new Patient(1L, "John Doe", 30, "Flu");
        patientDAO.addPatient(patient);

        Patient retrievedPatient = patientDAO.getPatientById(1L);
        assertNotNull(retrievedPatient, "Patient should exist before deletion");

        patientDAO.deletePatient(1L);
        Patient deletedPatient = patientDAO.getPatientById(1L);
        assertNull(deletedPatient, "Patient should not exist after deletion");
    }

    @Test
    void testGetPatientById_NotFound() throws DataAccessException { // Updated signature
        Patient retrievedPatient = patientDAO.getPatientById(99L); // Assuming 99L does not exist
        assertNull(retrievedPatient);
    }

    // Example of how you might test for DataAccessException if you could reliably cause it
    // @Test
    // void testOperationOnClosedConnection() throws DataAccessException {
    //     patientDAO.closeConnection(); // Close the connection
    //     assertThrows(DataAccessException.class, () -> {
    //         patientDAO.addPatient(new Patient(5L, "Ghost", 0, "None"));
    //     });
    //     // Re-initialize for other tests or ensure @AfterEach handles this state
    //     patientDAO = new PatientDAOImpl(); // Or ensure tests are independent
    // }
}
