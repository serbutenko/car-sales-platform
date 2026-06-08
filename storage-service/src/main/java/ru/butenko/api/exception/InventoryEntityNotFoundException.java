package ru.butenko.api.exception;

import java.util.UUID;

public class InventoryEntityNotFoundException extends RuntimeException {
    public InventoryEntityNotFoundException(String entityName, UUID id) {
        super(entityName + " not found: " + id);
    }
}
