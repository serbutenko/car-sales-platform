package ru.butenko.api.dto;

import java.util.UUID;

public record CreateComponentRequest(
        UUID componentOptionId,
        int quantity
) {
}
