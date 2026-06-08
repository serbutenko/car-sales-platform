package ru.butenko.integration.grpc;

public class StorageCarNotFoundException extends RuntimeException {
    public StorageCarNotFoundException(String message) {
        super(message == null || message.isBlank() ? "Storage car not found" : message);
    }
}
