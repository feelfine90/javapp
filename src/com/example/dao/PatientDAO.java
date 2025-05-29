package com.example.dao;

import com.example.model.Patient;
import com.example.exception.DataAccessException;
import java.util.List;

public interface PatientDAO {
    void addPatient(Patient patient) throws DataAccessException;
    Patient getPatientById(long id) throws DataAccessException;
    List<Patient> getAllPatients() throws DataAccessException;
    void updatePatient(Patient patient) throws DataAccessException;
    void deletePatient(long id) throws DataAccessException;
}
