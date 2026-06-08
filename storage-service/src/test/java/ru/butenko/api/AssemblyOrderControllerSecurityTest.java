package ru.butenko.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import ru.butenko.api.controller.AssemblyOrderController;
import ru.butenko.api.dto.AssemblyOrderResponse;
import ru.butenko.application.service.AssemblyOrderService;
import ru.butenko.config.SecurityConfig;
import ru.butenko.domain.assembly.AssemblyOrderStatus;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssemblyOrderController.class)
@Import(SecurityConfig.class)
class AssemblyOrderControllerSecurityTest {

    private static final UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SOURCE_ORDER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssemblyOrderService assemblyOrderService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void listAssemblyOrders_shouldReturn401_whenAnonymous() throws Exception {
        mockMvc.perform(get("/api/assembly-orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listAssemblyOrders_shouldBeForbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/assembly-orders")
                        .with(auth("USER")))
                .andExpect(status().isForbidden());

        verify(assemblyOrderService, never()).findAll();
    }

    @Test
    void listAssemblyOrders_shouldBeForbiddenForManager() throws Exception {
        mockMvc.perform(get("/api/assembly-orders")
                        .with(auth("MANAGER")))
                .andExpect(status().isForbidden());

        verify(assemblyOrderService, never()).findAll();
    }

    @Test
    void createAssemblyOrder_shouldBeAllowedForWarehouseAdmin() throws Exception {
        when(assemblyOrderService.create(any())).thenReturn(response());

        mockMvc.perform(post("/api/assembly-orders")
                        .with(auth("WAREHOUSE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceOrderId": "22222222-2222-2222-2222-222222222222",
                                  "sourceOrderType": "STOCK"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(assemblyOrderService).create(any());
    }

    @Test
    void createAssemblyOrder_shouldBeAllowedForAdmin() throws Exception {
        when(assemblyOrderService.create(any())).thenReturn(response());

        mockMvc.perform(post("/api/assembly-orders")
                        .with(auth("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceOrderId": "22222222-2222-2222-2222-222222222222",
                                  "sourceOrderType": "STOCK"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(assemblyOrderService).create(any());
    }

    @Test
    void assignAssemblyOrder_shouldBeAllowedForWarehouseAdmin() throws Exception {
        when(assemblyOrderService.assignWarehouseAdmin(any(), any())).thenReturn(response());

        mockMvc.perform(patch("/api/assembly-orders/{id}/assignee", ORDER_ID)
                        .with(auth("WAREHOUSE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "warehouseAdminId": "66666666-6666-6666-6666-666666666666"
                                }
                                """))
                .andExpect(status().isOk());

        verify(assemblyOrderService).assignWarehouseAdmin(any(), any());
    }

    private AssemblyOrderResponse response() {
        Instant now = Instant.parse("2026-05-09T12:00:00Z");
        return new AssemblyOrderResponse(
                ORDER_ID,
                SOURCE_ORDER_ID,
                "STOCK",
                null,
                null,
                java.util.List.of(),
                AssemblyOrderStatus.CREATED,
                null,
                now,
                now,
                false
        );
    }

    private RequestPostProcessor auth(String... roles) {
        GrantedAuthority[] authorities = java.util.Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toArray(GrantedAuthority[]::new);

        return jwt().authorities(authorities);
    }
}
