package ru.butenko.api.dto;

import java.util.UUID;

public record UpdateComponentRequest(
        UUID componentOptionId,
        int quantity,
        int reservedQuantity
) {
}
