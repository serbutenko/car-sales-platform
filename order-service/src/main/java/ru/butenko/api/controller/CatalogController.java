package ru.butenko.api.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.butenko.api.dto.request.CarFilterRequest;
import ru.butenko.api.dto.response.CarResponse;
import ru.butenko.api.dto.response.ErrorResponse;
import ru.butenko.api.mapper.CarDtoMapper;
import ru.butenko.application.service.CatalogService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cars")
@Tag(name = "Catalog", description = "Операции каталога: просмотр автомобиля по id и фильтрация списка машин")
public class CatalogController {
    private final CatalogService catalogService;
    private final CarDtoMapper carDtoMapper;

    @PreAuthorize("hasRole('USER') or hasRole('WAREHOUSE_ADMIN') or hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Получить автомобиль по id",
            description = "Возвращает карточку автомобиля из каталога по его идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Автомобиль найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный id или автомобиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CarResponse getCar(@Parameter(description = "Идентификатор автомобиля") @PathVariable UUID id) {
        return carDtoMapper.toResponse(catalogService.getCar(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('WAREHOUSE_ADMIN')")
    @GetMapping
    @Operation(summary = "Получить список автомобилей с фильтрацией",
            description = "Поддерживает фильтрацию по цене, марке, модели, характеристикам и совместимым комплектующим")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры фильтра",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<CarResponse> listCars(@Parameter(description = "Параметры фильтрации каталога") CarFilterRequest filter) {
        return catalogService.listCars(carDtoMapper.toDomain(filter)).stream()
                .map(carDtoMapper::toResponse)
                .toList();
    }

}
