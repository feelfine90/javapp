package com.example.dao;

import com.example.model.Patient;
import java.util.List;

public interface PatientDAO {
    void addPatient(Patient patient);
    Patient getPatientById(long id);
    List<Patient> getAllPatients();
    void updatePatient(Patient patient);
    void deletePatient(long id);
}
