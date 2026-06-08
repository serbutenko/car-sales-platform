package ru.butenko.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.butenko.api.dto.response.AvailableCarResponse;
import ru.butenko.api.dto.response.ErrorResponse;
import ru.butenko.api.mapper.AvailableCarMapper;
import ru.butenko.integration.grpc.StorageCarGrpcClient;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
@Tag(name = "Available cars", description = "Автомобили в наличии, полученные из StorageService по gRPC")
public class AvailableCarController {
    private final StorageCarGrpcClient storageCarGrpcClient;
    private final AvailableCarMapper availableCarMapper;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Получить список автомобилей в наличии",
            description = "Запрашивает актуальный список доступных к продаже автомобилей из StorageService по gRPC")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "StorageService недоступен или не ответил вовремя",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<AvailableCarResponse> listAvailableCars() {
        return storageCarGrpcClient.listAvailableCars().stream()
                .map(availableCarMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Получить автомобиль в наличии по id",
            description = "Запрашивает конкретный доступный к продаже автомобиль из StorageService по gRPC")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Автомобиль найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Автомобиль в наличии не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "StorageService недоступен или не ответил вовремя",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AvailableCarResponse getAvailableCar(
            @Parameter(description = "Идентификатор автомобиля") @PathVariable UUID id
    ) {
        return availableCarMapper.toResponse(storageCarGrpcClient.getAvailableCar(id));
    }
}
