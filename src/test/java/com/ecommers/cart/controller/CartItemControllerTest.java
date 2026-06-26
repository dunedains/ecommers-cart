package com.ecommers.cart.controller;

import com.ecommers.cart.dto.CartItemDto.CartResponse;
import com.ecommers.cart.service.CartItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartItemController.class)
class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartItemService service;

    @Test
    @DisplayName("POST /api/cart -> 201")
    void addToCart_devuelve201() throws Exception {
        when(service.addToCart(any())).thenReturn(new CartResponse(1L, 2L, 10L, 3));

        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":2,\"productId\":10,\"quantity\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    @DisplayName("POST /api/cart inválido (cantidad < 1) -> 400")
    void addToCart_invalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":2,\"productId\":10,\"quantity\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/cart/user/{userId} -> 200")
    void getCartByUser_devuelve200() throws Exception {
        when(service.getCartByUser(2L)).thenReturn(List.of(new CartResponse(1L, 2L, 10L, 3)));

        mockMvc.perform(get("/api/cart/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2));
    }

    @Test
    @DisplayName("PATCH /api/cart/{id}/quantity -> 200")
    void updateQuantity_devuelve200() throws Exception {
        when(service.updateQuantity(eq(1L), eq(5))).thenReturn(new CartResponse(1L, 2L, 10L, 5));

        mockMvc.perform(patch("/api/cart/1/quantity").param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    @DisplayName("DELETE /api/cart/{id} -> 204")
    void removeFromCart_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/cart/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/cart/user/{userId} -> 204")
    void clearCart_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/cart/user/2"))
                .andExpect(status().isNoContent());
    }
}
