package com.ecommers.cart.service;

import com.ecommers.cart.dto.CartItemDto.CartRequest;
import com.ecommers.cart.dto.CartItemDto.CartResponse;

import java.util.List;

public interface CartItemService {
    CartResponse addToCart(CartRequest request);
    List<CartResponse> getCartByUser(Long userId);
    CartResponse updateQuantity(Long id, Integer quantity);
    void removeFromCart(Long id);
    void clearCart(Long userId);
}
