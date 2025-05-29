package com.example.service;

import com.example.dao.PatientDAO;
import com.example.model.Patient;
import java.util.List;

public class PatientService {

    private PatientDAO patientDAO;

    public PatientService(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    public void addPatient(Patient patient) {
        // Future validation logic can be added here
        patientDAO.addPatient(patient);
    }

    public Patient getPatientById(long id) {
        return patientDAO.getPatientById(id);
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public void updatePatient(Patient patient) {
        // Future validation logic can be added here
        patientDAO.updatePatient(patient);
    }

    public void deletePatient(long id) {
        patientDAO.deletePatient(id);
    }
}
