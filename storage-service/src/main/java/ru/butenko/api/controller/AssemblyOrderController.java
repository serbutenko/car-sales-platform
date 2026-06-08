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
import org.springframework.web.bind.annotation.*;
import ru.butenko.api.dto.AssignAssemblyOrderRequest;
import ru.butenko.api.dto.AssemblyOrderResponse;
import ru.butenko.api.dto.CreateAssemblyOrderRequest;
import ru.butenko.api.dto.ErrorResponse;
import ru.butenko.api.dto.UpdateAssemblyOrderRequest;
import ru.butenko.api.dto.UpdateAssemblyOrderStatusRequest;
import ru.butenko.application.service.AssemblyOrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assembly-orders")
@RequiredArgsConstructor
@Tag(name = "Assembly orders", description = "Операции со складскими заказами на сборку автомобиля")
public class AssemblyOrderController {
    private final AssemblyOrderService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Создать заказ на сборку",
            description = "Создаёт внутренний складской заказ на сборку или резервирование автомобиля")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заказ на сборку создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AssemblyOrderResponse create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные складского заказа на сборку",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateAssemblyOrderRequest.class))
            )
            @RequestBody CreateAssemblyOrderRequest request) {
        return service.create(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Получить список заказов на сборку",
            description = "Возвращает все неудалённые внутренние складские заказы на сборку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<AssemblyOrderResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Получить заказ на сборку по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ на сборку найден"),
            @ApiResponse(responseCode = "400", description = "Заказ не найден или id некорректен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AssemblyOrderResponse findById(@Parameter(description = "Идентификатор заказа на сборку") @PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Обновить заказ на сборку",
            description = "Полностью обновляет редактируемые поля внутреннего складского заказа")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ на сборку обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или заказ не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AssemblyOrderResponse update(
            @Parameter(description = "Идентификатор заказа на сборку") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные заказа на сборку",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateAssemblyOrderRequest.class))
            )
            @RequestBody UpdateAssemblyOrderRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Изменить статус заказа на сборку",
            description = "Переводит заказ на сборку в новый складской статус")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или заказ не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AssemblyOrderResponse updateStatus(
            @Parameter(description = "Идентификатор заказа на сборку") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый статус заказа на сборку",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateAssemblyOrderStatusRequest.class))
            )
            @RequestBody UpdateAssemblyOrderStatusRequest request) {
        return service.updateStatus(id, request.status());
    }

    @PatchMapping("/{id}/assignee")
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Назначить ответственного склада",
            description = "Назначает сотрудника склада ответственным за выполнение заказа на сборку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответственный назначен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или заказ не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AssemblyOrderResponse assignWarehouseAdmin(
            @Parameter(description = "Идентификатор заказа на сборку") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Идентификатор ответственного сотрудника склада",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AssignAssemblyOrderRequest.class))
            )
            @RequestBody AssignAssemblyOrderRequest request) {
        return service.assignWarehouseAdmin(id, request.warehouseAdminId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Удалить заказ на сборку",
            description = "Помечает заказ на сборку как удалённый")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заказ на сборку удалён"),
            @ApiResponse(responseCode = "400", description = "Заказ не найден или id некорректен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void delete(@Parameter(description = "Идентификатор заказа на сборку") @PathVariable UUID id) {
        service.delete(id);
    }
}
