package com.example.service;

import com.example.dao.PatientDAO;
import com.example.model.Patient;
import com.example.exception.DataAccessException;
import com.example.exception.ServiceException;
import com.example.exception.PatientNotFoundException;
import java.util.List;

public class PatientService {

    private PatientDAO patientDAO;

    public PatientService(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    public void addPatient(Patient patient) throws ServiceException {
        try {
            // Future validation logic can be added here
            // For example, check if patient object is valid or if ID already exists
            if (patient == null) {
                throw new ServiceException("Patient object cannot be null.");
            }
            // Potentially check if a patient with this ID already exists if IDs are client-generated
            // Patient existingPatient = patientDAO.getPatientById(patient.getId());
            // if (existingPatient != null) {
            //     throw new ServiceException("Patient with ID " + patient.getId() + " already exists.");
            // }
            patientDAO.addPatient(patient);
        } catch (DataAccessException e) {
            throw new ServiceException("Service error while adding patient: " + (patient != null ? patient.getName() : "null"), e);
        }
    }

    public Patient getPatientById(long id) throws ServiceException, PatientNotFoundException {
        try {
            Patient patient = patientDAO.getPatientById(id);
            if (patient == null) {
                throw new PatientNotFoundException("Patient with ID " + id + " not found.");
            }
            return patient;
        } catch (DataAccessException e) {
            throw new ServiceException("Service error while retrieving patient with ID " + id, e);
        }
    }

    public List<Patient> getAllPatients() throws ServiceException {
        try {
            return patientDAO.getAllPatients();
        } catch (DataAccessException e) {
            throw new ServiceException("Service error while retrieving all patients.", e);
        }
    }

    public void updatePatient(Patient patient) throws ServiceException, PatientNotFoundException {
        try {
            if (patient == null) {
                throw new ServiceException("Patient object cannot be null for update.");
            }
            Patient existingPatient = patientDAO.getPatientById(patient.getId());
            if (existingPatient == null) {
                throw new PatientNotFoundException("Cannot update. Patient with ID " + patient.getId() + " not found.");
            }
            // Add any other validation for patient data before update if necessary
            patientDAO.updatePatient(patient);
        } catch (DataAccessException e) {
            throw new ServiceException("Service error while updating patient with ID " + (patient != null ? patient.getId() : "null"), e);
        }
        // PatientNotFoundException from the check will propagate
    }

    public void deletePatient(long id) throws ServiceException, PatientNotFoundException {
        try {
            Patient existingPatient = patientDAO.getPatientById(id);
            if (existingPatient == null) {
                throw new PatientNotFoundException("Cannot delete. Patient with ID " + id + " not found.");
            }
            patientDAO.deletePatient(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Service error while deleting patient with ID " + id, e);
        }
        // PatientNotFoundException from the check will propagate
    }
}
