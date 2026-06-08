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
import ru.butenko.api.controller.OrderController;
import ru.butenko.api.exception.GlobalExceptionHandler;
import ru.butenko.api.mapper.ConfiguratorDtoMapper;
import ru.butenko.api.mapper.OrderDtoMapper;
import ru.butenko.application.service.OrderService;
import ru.butenko.config.SecurityConfig;
import ru.butenko.domain.orders.stock.StockOrder;
import ru.butenko.security.CurrentUserService;
import ru.butenko.security.OrderSecurityService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, OrderDtoMapper.class, ConfiguratorDtoMapper.class})
class OrderControllerSecurityTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MANAGER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ORDER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID CAR_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CurrentUserService currentUserService;

    @MockBean(name = "orderSecurity")
    private OrderSecurityService orderSecurityService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void createStockOrder_shouldReturn401_whenAnonymous() throws Exception {
        mockMvc.perform(post("/api/orders/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"carId":"44444444-4444-4444-4444-444444444444"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createStockOrder_shouldBeAllowedForUser() throws Exception {
        when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
        when(orderService.createStockOrder(USER_ID, CAR_ID))
                .thenReturn(new StockOrder(ORDER_ID, USER_ID, MANAGER_ID, CAR_ID));

        mockMvc.perform(post("/api/orders/stock")
                        .with(auth("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"carId":"44444444-4444-4444-4444-444444444444"}
                                """))
                .andExpect(status().isOk());

        verify(orderService).createStockOrder(USER_ID, CAR_ID);
    }

    @Test
    void approveStockOrder_shouldBeAllowedForManager() throws Exception {
        mockMvc.perform(post("/api/orders/stock/{id}/approve", ORDER_ID)
                        .with(auth("MANAGER")))
                .andExpect(status().isNoContent());

        verify(orderService).stockApproveByManager(ORDER_ID);
    }

    @Test
    void approveStockOrder_shouldBeForbiddenForUser() throws Exception {
        mockMvc.perform(post("/api/orders/stock/{id}/approve", ORDER_ID)
                        .with(auth("USER")))
                .andExpect(status().isForbidden());

        verify(orderService, never()).stockApproveByManager(ORDER_ID);
    }

    @Test
    void approveCustomOrder_shouldBeAllowedForWarehouseAdmin() throws Exception {
        mockMvc.perform(post("/api/orders/custom/{id}/approve", ORDER_ID)
                        .with(auth("WAREHOUSE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(orderService).customApprovedByStock(ORDER_ID);
    }

    @Test
    void getStockOrder_shouldBeAllowedForOwnerUser() throws Exception {
        when(orderSecurityService.isStockOrderOwner(ORDER_ID)).thenReturn(true);
        when(orderService.getStockOrder(ORDER_ID))
                .thenReturn(new StockOrder(ORDER_ID, USER_ID, MANAGER_ID, CAR_ID));

        mockMvc.perform(get("/api/orders/stock/{id}", ORDER_ID)
                        .with(auth("USER")))
                .andExpect(status().isOk());

        verify(orderService).getStockOrder(ORDER_ID);
    }

    @Test
    void getStockOrder_shouldBeForbiddenForNonOwnerUser() throws Exception {
        when(orderSecurityService.isStockOrderOwner(ORDER_ID)).thenReturn(false);

        mockMvc.perform(get("/api/orders/stock/{id}", ORDER_ID)
                        .with(auth("USER")))
                .andExpect(status().isForbidden());

        verify(orderService, never()).getStockOrder(ORDER_ID);
    }

    @Test
    void listStockOrders_shouldBeAllowedForUser() throws Exception {
        when(orderService.listStockOrders()).thenReturn(List.of());

        mockMvc.perform(get("/api/orders/stock")
                        .with(auth("USER")))
                .andExpect(status().isOk());

        verify(orderService).listStockOrders();
    }

    @Test
    void listStockOrders_shouldBeAllowedForManager() throws Exception {
        when(orderService.listStockOrders()).thenReturn(List.of());

        mockMvc.perform(get("/api/orders/stock")
                        .with(auth("MANAGER")))
                .andExpect(status().isOk());

        verify(orderService).listStockOrders();
    }

    private RequestPostProcessor auth(String... roles) {
        GrantedAuthority[] authorities = java.util.Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toArray(GrantedAuthority[]::new);

        return jwt().authorities(authorities);
    }
}
