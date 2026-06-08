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
import ru.butenko.api.dto.CreateComponentRequest;
import ru.butenko.api.dto.ErrorResponse;
import ru.butenko.api.dto.ComponentResponse;
import ru.butenko.api.dto.UpdateComponentRequest;
import ru.butenko.application.service.InventoryManagementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage/components")
@RequiredArgsConstructor
@Tag(name = "Components", description = "Операции складского учёта комплектующих")
public class ComponentController {
    private final InventoryManagementService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Добавить комплектующую на склад",
            description = "Создаёт складскую запись комплектующей и фиксирует доступное количество")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Комплектующая добавлена на склад"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ComponentResponse create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные комплектующей для складского учёта",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateComponentRequest.class))
            )
            @RequestBody CreateComponentRequest request) {
        return service.createComponent(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Получить список комплектующих на складе",
            description = "Возвращает все неудалённые складские записи комплектующих")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<ComponentResponse> findAll() {
        return service.findAllComponents();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Получить складскую комплектующую по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комплектующая найдена"),
            @ApiResponse(responseCode = "400", description = "Комплектующая не найдена или id некорректен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ComponentResponse findById(@Parameter(description = "Идентификатор складской записи комплектующей") @PathVariable UUID id) {
        return service.findComponentById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Обновить складскую комплектующую",
            description = "Обновляет стабильный идентификатор детали и количество комплектующей на складе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комплектующая обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или комплектующая не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ComponentResponse update(
            @Parameter(description = "Идентификатор складской записи комплектующей") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные комплектующей на складе",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateComponentRequest.class))
            )
            @RequestBody UpdateComponentRequest request) {
        return service.updateComponent(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Удалить складскую комплектующую",
            description = "Помечает складскую запись комплектующей как удалённую")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Комплектующая удалена"),
            @ApiResponse(responseCode = "400", description = "Комплектующая не найдена или id некорректен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void delete(@Parameter(description = "Идентификатор складской записи комплектующей") @PathVariable UUID id) {
        service.deleteComponent(id);
    }
}
