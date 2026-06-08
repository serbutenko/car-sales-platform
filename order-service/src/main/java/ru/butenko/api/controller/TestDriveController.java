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
import ru.butenko.api.dto.request.CreateTestDriveRequestDto;
import ru.butenko.api.dto.response.ErrorResponse;
import ru.butenko.api.dto.response.TestDriveCarsResponse;
import ru.butenko.api.dto.response.TestDriveRequestResponse;
import ru.butenko.api.mapper.TestDriveDtoMapper;
import ru.butenko.application.service.TestDriveService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test-drives")
@Tag(name = "Test Drive", description = "Управление автомобилями для тест-драйва и заявками клиентов")
public class TestDriveController {

    private final TestDriveService testDriveService;
    private final TestDriveDtoMapper mapper;

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping("/cars/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Добавить автомобиль в список доступных для тест-драйва")
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
    public void addCar(@Parameter(description = "Идентификатор автомобиля") @PathVariable UUID carId) {
        testDriveService.addCarToTestDrive(carId);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/cars/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Убрать автомобиль из списка доступных для тест-драйва")
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
    public void removeCar(@Parameter(description = "Идентификатор автомобиля") @PathVariable UUID carId) {
        testDriveService.removeCarFromTestDrive(carId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/cars")
    @Operation(summary = "Получить список автомобилей, доступных для тест-драйва")
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
    public TestDriveCarsResponse listCars() {
        return new TestDriveCarsResponse(testDriveService.listTestDriveCars());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/requests")
    @Operation(summary = "Создать заявку на тест-драйв")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или занятый слот",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TestDriveRequestResponse createRequest(
            @RequestBody(description = "Идентификаторы клиента, автомобиля и дата начала тест-драйва", required = true,
                    content = @Content(schema = @Schema(implementation = CreateTestDriveRequestDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateTestDriveRequestDto request) {
        return mapper.toResponse(
                testDriveService.createRequest(
                        request.clientId(),
                        request.carId(),
                        request.startAt()
                )
        );
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/requests")
    @Operation(summary = "Получить список заявок на тест-драйв")
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
    public List<TestDriveRequestResponse> listRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return testDriveService.listRequests(page, size).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/requests/{id}")
    @Operation(summary = "Получить заявку на тест-драйв по id")
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
    public TestDriveRequestResponse getRequest(@Parameter(description = "Идентификатор заявки") @PathVariable UUID id) {
        return mapper.toResponse(testDriveService.getRequest(id));
    }
}
