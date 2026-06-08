package ru.butenko.api.dto;

import ru.butenko.domain.assembly.AssemblyOrderStatus;

public record UpdateAssemblyOrderStatusRequest(
        AssemblyOrderStatus status
) {
}
