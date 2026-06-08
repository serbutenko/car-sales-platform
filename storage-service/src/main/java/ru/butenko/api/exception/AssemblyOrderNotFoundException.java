package ru.butenko.api.exception;

import java.util.UUID;

public class AssemblyOrderNotFoundException extends RuntimeException {
    public AssemblyOrderNotFoundException(UUID id) {
        super("Assembly order not found: " + id);
    }
}
