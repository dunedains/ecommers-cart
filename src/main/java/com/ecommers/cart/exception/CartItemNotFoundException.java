package com.ecommers.cart.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(Long id) {
        super("CartItem no encontrado con id: " + id);
    }
}