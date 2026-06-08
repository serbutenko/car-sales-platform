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
import ru.butenko.api.controller.CarController;
import ru.butenko.api.controller.ComponentController;
import ru.butenko.application.service.InventoryManagementService;
import ru.butenko.config.SecurityConfig;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CarController.class, ComponentController.class})
@Import(SecurityConfig.class)
class InventoryControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryManagementService inventoryManagementService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void listCars_shouldReturn401_whenAnonymous() throws Exception {
        mockMvc.perform(get("/api/storage/cars"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listCars_shouldBeForbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/storage/cars")
                        .with(auth("USER")))
                .andExpect(status().isForbidden());

        verify(inventoryManagementService, never()).findAllCars();
    }

    @Test
    void listCars_shouldBeAllowedForWarehouseAdmin() throws Exception {
        when(inventoryManagementService.findAllCars()).thenReturn(List.of());

        mockMvc.perform(get("/api/storage/cars")
                        .with(auth("WAREHOUSE_ADMIN")))
                .andExpect(status().isOk());

        verify(inventoryManagementService).findAllCars();
    }

    private RequestPostProcessor auth(String... roles) {
        GrantedAuthority[] authorities = java.util.Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toArray(GrantedAuthority[]::new);

        return jwt().authorities(authorities);
    }
}
