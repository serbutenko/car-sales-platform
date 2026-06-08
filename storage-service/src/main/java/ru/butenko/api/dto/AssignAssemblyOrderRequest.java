package ru.butenko.api.dto;

import java.util.UUID;

public record AssignAssemblyOrderRequest(
        UUID warehouseAdminId
) {
}
