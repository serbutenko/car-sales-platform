package ru.butenko.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.butenko.api.dto.request.CreateCustomOrderRequest;
import ru.butenko.api.dto.request.CreateStockOrderRequest;
import ru.butenko.api.dto.response.CustomOrderResponse;
import ru.butenko.api.dto.response.ErrorResponse;
import ru.butenko.api.dto.response.StockOrderResponse;
import ru.butenko.api.mapper.ConfiguratorDtoMapper;
import ru.butenko.api.mapper.OrderDtoMapper;
import ru.butenko.application.service.OrderService;
import ru.butenko.security.CurrentUserService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Создание заказов и перевод их по шагам бизнес-процесса")
public class OrderController {
    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;
    private final CurrentUserService currentUserService;
    private final ConfiguratorDtoMapper configuratorDtoMapper;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/stock")
    @Operation(summary = "Создать заказ автомобиля со склада",
            description = "Создаёт заказ на существующий автомобиль из наличия и резервирует его")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или автомобиль недоступен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public StockOrderResponse createStockOrder(
            @RequestBody(description = "Идентификаторы клиента и автомобиля из наличия", required = true,
                    content = @Content(schema = @Schema(implementation = CreateStockOrderRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateStockOrderRequest request) {
        UUID clientId = currentUserService.getCurrentUserId();
        return orderDtoMapper.toResponse(
                orderService.createStockOrder(clientId, request.carId())
        );
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or @orderSecurity.isStockOrderOwner(#id)")
    @GetMapping("/stock/{id}")
    @Operation(summary = "Получить заказ со склада по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ успешно найден"),
            @ApiResponse(responseCode = "400", description = "Заказ не найден или id некорректен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public StockOrderResponse getStockOrder(@Parameter(description = "Идентификатор заказа") @PathVariable UUID id) {
        return orderDtoMapper.toResponse(orderService.getStockOrder(id));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/stock/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Подтвердить заказ со склада менеджером")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void approveStockOrder(@PathVariable UUID id) {
        orderService.stockApproveByManager(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/stock/{id}/request-payment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Запросить оплату заказа со склада")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void requestStockPayment(@PathVariable UUID id) {
        orderService.stockRequestPayment(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isStockOrderOwner(#id)")
    @PostMapping("/stock/{id}/pay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Оплатить заказ со склада")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void payStockOrder(@PathVariable UUID id) {
        orderService.stockPay(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/stock/{id}/ready")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отметить заказ со склада готовым к выдаче")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void readyStockOrder(@PathVariable UUID id) {
        orderService.stockReadyForDelivery(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/stock/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Завершить заказ со склада")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void completeStockOrder(@PathVariable UUID id) {
        orderService.stockComplete(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isStockOrderOwner(#id)")
    @PostMapping("/stock/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отменить заказ со склада")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void cancelStockOrder(@PathVariable UUID id) {
        orderService.stockCancel(id);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/custom")
    @Operation(summary = "Создать кастомный заказ",
            description = "Создаёт заказ на пользовательскую конфигурацию автомобиля")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Кастомный заказ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или невалидная конфигурация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CustomOrderResponse createCustomOrder(
            @RequestBody(description = "Идентификатор клиента и параметры конфигурации", required = true,
                    content = @Content(schema = @Schema(implementation = CreateCustomOrderRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateCustomOrderRequest request) {
        UUID clientId = currentUserService.getCurrentUserId();
        return orderDtoMapper.toResponse(
                orderService.createCustomOrder(
                        clientId,
                        configuratorDtoMapper.toDomain(request.configuration())
                )
        );
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or @orderSecurity.isCustomOrderOwner(#id)")
    @GetMapping("/custom/{id}")
    @Operation(summary = "Получить кастомный заказ по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ успешно найден"),
            @ApiResponse(responseCode = "400", description = "Заказ не найден или id некорректен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CustomOrderResponse getCustomOrder(@Parameter(description = "Идентификатор заказа") @PathVariable UUID id) {
        return orderDtoMapper.toResponse(orderService.getCustomOrder(id));
    }

    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    @PostMapping("/custom/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Подтвердить кастомный заказ складом")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void approveCustomOrder(@PathVariable UUID id) {
        orderService.customApprovedByStock(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/custom/{id}/request-payment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Запросить оплату кастомного заказа")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void requestCustomPayment(@PathVariable UUID id) {
        orderService.customRequestPayment(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isCustomOrderOwner(#id)")
    @PostMapping("/custom/{id}/pay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Оплатить кастомный заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void payCustomOrder(@PathVariable UUID id) {
        orderService.customPay(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/custom/{id}/wait-delivery")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Перевести кастомный заказ в ожидание доставки")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void waitCustomDelivery(@PathVariable UUID id) {
        orderService.customWaitForDelivery(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/custom/{id}/ready")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отметить кастомный заказ готовым к выдаче")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void readyCustomOrder(@PathVariable UUID id) {
        orderService.customReadyForDelivery(id);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/custom/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Завершить кастомный заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void completeCustomOrder(@PathVariable UUID id) {
        orderService.customComplete(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isCustomOrderOwner(#id)")
    @PostMapping("/custom/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отменить кастомный заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void cancelCustomOrder(@PathVariable UUID id) {
        orderService.customCancel(id);
    }

    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/stock")
    @Operation(summary = "Получить заказ клиента со склада")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заказов получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<StockOrderResponse> listStockOrders() {
        return orderService.listStockOrders().stream()
                .map(orderDtoMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/custom")
    @Operation(summary = "Получить кастомный заказ клиента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заказов получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<CustomOrderResponse> listCustomOrders() {
        return orderService.listCustomOrders().stream()
                .map(orderDtoMapper::toResponse)
                .toList();
    }
}
