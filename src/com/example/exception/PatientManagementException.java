package com.example.exception;

public class PatientManagementException extends Exception {
    public PatientManagementException(String message) {
        super(message);
    }

    public PatientManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
