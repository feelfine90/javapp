package com.example.dao;

import com.example.model.Patient;
import com.example.exception.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    private static final String JDBC_URL = "jdbc:h2:mem:patientdb;DB_CLOSE_DELAY=-1";
    // No specific user/password needed for this H2 in-memory setup
    private Connection connection;

    public PatientDAOImpl() throws DataAccessException {
        try {
            connection = DriverManager.getConnection(JDBC_URL);
            setupTable();
        } catch (SQLException e) {
            // Log or print stack trace for diagnosis if necessary
            // e.printStackTrace(); 
            throw new DataAccessException("Error initializing database connection or setting up table", e);
        }
    }

    private void setupTable() throws SQLException {
        // This method is called internally, SQLException is fine here, 
        // constructor will wrap it if it occurs.
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS patients (" +
                    "ID BIGINT PRIMARY KEY, " +
                    "NAME VARCHAR(255), " +
                    "AGE INT, " +
                    "MEDICALHISTORY VARCHAR(1000))");
        }
    }

    @Override
    public void addPatient(Patient patient) throws DataAccessException {
        String sql = "INSERT INTO patients (ID, NAME, AGE, MEDICALHISTORY) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, patient.getId());
            pstmt.setString(2, patient.getName());
            pstmt.setInt(3, patient.getAge());
            pstmt.setString(4, patient.getMedicalHistory());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error adding patient with ID: " + patient.getId(), e);
        }
    }

    @Override
    public Patient getPatientById(long id) throws DataAccessException {
        String sql = "SELECT * FROM patients WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Patient(
                        rs.getLong("ID"),
                        rs.getString("NAME"),
                        rs.getInt("AGE"),
                        rs.getString("MEDICALHISTORY"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving patient with ID: " + id, e);
        }
        return null; // Return null if not found, service layer will handle PatientNotFoundException
    }

    @Override
    public List<Patient> getAllPatients() throws DataAccessException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                patients.add(new Patient(
                        rs.getLong("ID"),
                        rs.getString("NAME"),
                        rs.getInt("AGE"),
                        rs.getString("MEDICALHISTORY")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all patients", e);
        }
        return patients;
    }

    @Override
    public void updatePatient(Patient patient) throws DataAccessException {
        String sql = "UPDATE patients SET NAME = ?, AGE = ?, MEDICALHISTORY = ? WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getMedicalHistory());
            pstmt.setLong(4, patient.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                // This could be an indication that the patient ID didn't exist for update.
                // Depending on requirements, could throw an exception here or let it be.
                // For now, we'll assume the service layer might check existence before update if needed.
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating patient with ID: " + patient.getId(), e);
        }
    }

    @Override
    public void deletePatient(long id) throws DataAccessException {
        String sql = "DELETE FROM patients WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                 // Similar to update, if ID doesn't exist, no rows are deleted.
                 // Service layer might handle this.
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting patient with ID: " + id, e);
        }
    }

    public void closeConnection() throws DataAccessException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error closing database connection", e);
        }
    }
}
