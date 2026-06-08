package ru.butenko.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import ru.butenko.api.controller.AvailableCarController;
import ru.butenko.api.exception.GlobalExceptionHandler;
import ru.butenko.api.mapper.AvailableCarMapper;
import ru.butenko.config.SecurityConfig;
import ru.butenko.integration.grpc.StorageCar;
import ru.butenko.integration.grpc.StorageCarGrpcClient;
import ru.butenko.integration.grpc.StorageServiceUnavailableException;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvailableCarController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, AvailableCarMapper.class})
class AvailableCarControllerTest {

    private static final UUID CAR_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID MODEL_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageCarGrpcClient storageCarGrpcClient;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void listAvailableCars_shouldReturn401_whenAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isUnauthorized());

        verify(storageCarGrpcClient, never()).listAvailableCars();
    }

    @Test
    void listAvailableCars_shouldBeAllowedForUser() throws Exception {
        when(storageCarGrpcClient.listAvailableCars())
                .thenReturn(List.of(new StorageCar(CAR_ID, MODEL_ID, "VIN-1", "AVAILABLE")));

        mockMvc.perform(get("/api/v1/cars").with(auth("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(CAR_ID.toString()))
                .andExpect(jsonPath("$[0].modelId").value(MODEL_ID.toString()))
                .andExpect(jsonPath("$[0].vin").value("VIN-1"))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));

        verify(storageCarGrpcClient).listAvailableCars();
    }

    @Test
    void listAvailableCars_shouldBeAllowedForManager() throws Exception {
        when(storageCarGrpcClient.listAvailableCars()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/cars").with(auth("MANAGER")))
                .andExpect(status().isOk());

        verify(storageCarGrpcClient).listAvailableCars();
    }

    @Test
    void listAvailableCars_shouldBeForbiddenForWarehouseAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/cars").with(auth("WAREHOUSE_ADMIN")))
                .andExpect(status().isForbidden());

        verify(storageCarGrpcClient, never()).listAvailableCars();
    }

    @Test
    void getAvailableCar_shouldBeAllowedForAdmin() throws Exception {
        when(storageCarGrpcClient.getAvailableCar(CAR_ID))
                .thenReturn(new StorageCar(CAR_ID, MODEL_ID, "VIN-1", "AVAILABLE"));

        mockMvc.perform(get("/api/v1/cars/{id}", CAR_ID).with(auth("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CAR_ID.toString()))
                .andExpect(jsonPath("$.modelId").value(MODEL_ID.toString()))
                .andExpect(jsonPath("$.vin").value("VIN-1"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));

        verify(storageCarGrpcClient).getAvailableCar(CAR_ID);
    }

    @Test
    void listAvailableCars_shouldReturn503_whenStorageIsUnavailable() throws Exception {
        when(storageCarGrpcClient.listAvailableCars())
                .thenThrow(new StorageServiceUnavailableException("StorageService is unavailable", null));

        mockMvc.perform(get("/api/v1/cars").with(auth("USER")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }

    private RequestPostProcessor auth(String... roles) {
        GrantedAuthority[] authorities = java.util.Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toArray(GrantedAuthority[]::new);

        return jwt().authorities(authorities);
    }
}
