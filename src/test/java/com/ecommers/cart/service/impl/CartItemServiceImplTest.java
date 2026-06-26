package com.ecommers.cart.service.impl;

import com.ecommers.cart.client.InventoryClient;
import com.ecommers.cart.client.ProductClient;
import com.ecommers.cart.dto.CartItemDto.CartRequest;
import com.ecommers.cart.dto.CartItemDto.CartResponse;
import com.ecommers.cart.dto.CartItemDto.InventoryDto;
import com.ecommers.cart.exception.CartItemNotFoundException;
import com.ecommers.cart.model.CartItem;
import com.ecommers.cart.repository.CartItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del carrito.
 * Se mockean el repositorio y los clientes de productos e inventario.
 */
@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {

    @Mock
    private CartItemRepository repository;
    @Mock
    private ProductClient productClient;
    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private CartItemServiceImpl service;

    @Test
    @DisplayName("addToCart: con stock suficiente agrega el item al carrito")
    void addToCart_conStock_agrega() {
        // Given: hay 5 en stock y el item no existe aún
        when(inventoryClient.getStock(10L)).thenReturn(new InventoryDto(1L, 10L, 5));
        when(repository.findByUserIdAndProductId(2L, 10L)).thenReturn(Optional.empty());
        when(repository.save(any(CartItem.class))).thenAnswer(i -> {
            CartItem c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        // When: se agregan 3 unidades
        CartResponse response = service.addToCart(new CartRequest(2L, 10L, 3));

        // Then
        assertThat(response.quantity()).isEqualTo(3);
        verify(repository).save(any(CartItem.class));
    }

    @Test
    @DisplayName("addToCart: si el stock es insuficiente, lanza excepción y no guarda")
    void addToCart_stockInsuficiente_lanzaExcepcion() {
        // Given: solo 2 en stock
        when(inventoryClient.getStock(10L)).thenReturn(new InventoryDto(1L, 10L, 2));

        // When / Then: se piden 5
        assertThatThrownBy(() -> service.addToCart(new CartRequest(2L, 10L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("updateQuantity: una cantidad <= 0 es rechazada sin tocar la base de datos")
    void updateQuantity_invalida_lanzaExcepcion() {
        assertThatThrownBy(() -> service.updateQuantity(1L, 0))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).findById(any());
    }

    @Test
    @DisplayName("updateQuantity: si el item no existe, lanza CartItemNotFoundException")
    void updateQuantity_itemInexistente_lanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateQuantity(99L, 3))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    @DisplayName("removeFromCart: si el item no existe, lanza excepción y no borra")
    void removeFromCart_inexistente_lanzaExcepcion() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.removeFromCart(99L))
                .isInstanceOf(CartItemNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("addToCart: si el item ya existe, acumula la cantidad")
    void addToCart_itemExistente_acumula() {
        CartItem existente = new CartItem();
        existente.setId(1L);
        existente.setUserId(2L);
        existente.setProductId(10L);
        existente.setQuantity(2);
        when(inventoryClient.getStock(10L)).thenReturn(new InventoryDto(1L, 10L, 5));
        when(repository.findByUserIdAndProductId(2L, 10L)).thenReturn(Optional.of(existente));
        when(repository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartResponse response = service.addToCart(new CartRequest(2L, 10L, 3));

        assertThat(response.quantity()).isEqualTo(5); // 2 + 3
    }

    @Test
    @DisplayName("updateQuantity: con stock suficiente actualiza la cantidad")
    void updateQuantity_conStock_actualiza() {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setProductId(10L);
        item.setQuantity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryClient.getStock(10L)).thenReturn(new InventoryDto(1L, 10L, 10));
        when(repository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartResponse response = service.updateQuantity(1L, 4);

        assertThat(response.quantity()).isEqualTo(4);
    }

    @Test
    @DisplayName("getCartByUser: mapea los items del usuario")
    void getCartByUser_devuelveLista() {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setUserId(2L);
        item.setProductId(10L);
        item.setQuantity(3);
        when(repository.findByUserId(2L)).thenReturn(java.util.List.of(item));

        assertThat(service.getCartByUser(2L)).hasSize(1);
    }

    @Test
    @DisplayName("clearCart: vacía el carrito del usuario")
    void clearCart_borraTodo() {
        service.clearCart(2L);

        verify(repository).deleteByUserId(2L);
    }
}
