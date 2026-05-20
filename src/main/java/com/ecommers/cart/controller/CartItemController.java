package com.ecommers.cart.controller;

import com.ecommers.cart.dto.CartItemDto.CartRequest;
import com.ecommers.cart.dto.CartItemDto.CartResponse;
import com.ecommers.cart.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService service;

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addToCart(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartResponse>> getCartByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getCartByUser(userId));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<CartResponse> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        return ResponseEntity.ok(service.updateQuantity(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        service.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        service.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
