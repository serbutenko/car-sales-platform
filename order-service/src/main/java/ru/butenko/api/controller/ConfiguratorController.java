package ru.butenko.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.butenko.api.dto.request.ConfigurationRequestDto;
import ru.butenko.api.dto.response.CarConfigurationResponse;
import ru.butenko.api.dto.response.ErrorResponse;
import ru.butenko.api.mapper.ConfiguratorDtoMapper;
import ru.butenko.application.service.ConfiguratorService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/configurations")
@Tag(name = "Configurator", description = "Сборка пользовательской конфигурации автомобиля по модели и выбранным комплектующим")
public class ConfiguratorController {

    private final ConfiguratorService configuratorService;
    private final ConfiguratorDtoMapper mapper;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Собрать конфигурацию автомобиля",
            description = "Проверяет совместимость выбранных опций и возвращает итоговую конфигурацию с рассчитанной ценой")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Конфигурация невалидна или содержит несовместимые опции",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Права отсутствуют",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CarConfigurationResponse build(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Модель автомобиля и выбранные идентификаторы комплектующих",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ConfigurationRequestDto.class))
            )
            @RequestBody ConfigurationRequestDto request) {
        return mapper.toResponse(configuratorService.build(mapper.toDomain(request)));
    }
}
