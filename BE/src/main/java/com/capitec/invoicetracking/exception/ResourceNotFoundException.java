package com.capitec.invoicetracking.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String id) {
        super(resource+" with identity " + id + " not found!");
    }
}
