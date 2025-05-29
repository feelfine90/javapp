package com.example.dao;

import com.example.model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    private static final String JDBC_URL = "jdbc:h2:mem:patientdb;DB_CLOSE_DELAY=-1";
    private Connection connection;

    public PatientDAOImpl() {
        try {
            connection = DriverManager.getConnection(JDBC_URL);
            setupTable();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing database connection", e);
        }
    }

    private void setupTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS patients (" +
                    "ID BIGINT PRIMARY KEY, " +
                    "NAME VARCHAR(255), " +
                    "AGE INT, " +
                    "MEDICALHISTORY VARCHAR(1000))");
        }
    }

    @Override
    public void addPatient(Patient patient) {
        String sql = "INSERT INTO patients (ID, NAME, AGE, MEDICALHISTORY) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, patient.getId());
            pstmt.setString(2, patient.getName());
            pstmt.setInt(3, patient.getAge());
            pstmt.setString(4, patient.getMedicalHistory());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Patient getPatientById(long id) {
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
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Patient> getAllPatients() {
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
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public void updatePatient(Patient patient) {
        String sql = "UPDATE patients SET NAME = ?, AGE = ?, MEDICALHISTORY = ? WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getMedicalHistory());
            pstmt.setLong(4, patient.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePatient(long id) {
        String sql = "DELETE FROM patients WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to close the connection when the application shuts down
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
