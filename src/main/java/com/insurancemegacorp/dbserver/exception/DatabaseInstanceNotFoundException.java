package com.insurancemegacorp.dbserver.exception;

public class DatabaseInstanceNotFoundException extends RuntimeException {
    
    public DatabaseInstanceNotFoundException(String instanceName) {
        super("Database instance not found: " + instanceName);
    }
}