package ru.butenko.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.butenko.api.dto.CreateCarRequest;
import ru.butenko.api.dto.ErrorResponse;
import ru.butenko.api.dto.CarResponse;
import ru.butenko.api.dto.UpdateCarRequest;
import ru.butenko.application.service.InventoryManagementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage/cars")
@RequiredArgsConstructor
@Tag(name = "Cars", description = "Операции складского учёта автомобилей")
public class CarController {
    private final InventoryManagementService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Добавить автомобиль на склад",
            description = "Создаёт складскую запись автомобиля с VIN, моделью и текущим складским статусом")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Автомобиль добавлен на склад"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CarResponse create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные автомобиля для складского учёта",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCarRequest.class))
            )
            @RequestBody CreateCarRequest request) {
        return service.createCar(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Получить список автомобилей на складе",
            description = "Возвращает все неудалённые складские записи автомобилей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<CarResponse> findAll() {
        return service.findAllCars();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Получить складской автомобиль по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Автомобиль найден"),
            @ApiResponse(responseCode = "400", description = "Автомобиль не найден или id некорректен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CarResponse findById(@Parameter(description = "Идентификатор складской записи автомобиля") @PathVariable UUID id) {
        return service.findCarById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Обновить складской автомобиль",
            description = "Обновляет модель, VIN или статус автомобиля на складе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Автомобиль обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или автомобиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CarResponse update(
            @Parameter(description = "Идентификатор складской записи автомобиля") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные автомобиля на складе",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCarRequest.class))
            )
            @RequestBody UpdateCarRequest request) {
        return service.updateCar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Удалить складской автомобиль",
            description = "Помечает складскую запись автомобиля как удалённую")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Автомобиль удалён"),
            @ApiResponse(responseCode = "400", description = "Автомобиль не найден или id некорректен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void delete(@Parameter(description = "Идентификатор складской записи автомобиля") @PathVariable UUID id) {
        service.deleteCar(id);
    }
}
