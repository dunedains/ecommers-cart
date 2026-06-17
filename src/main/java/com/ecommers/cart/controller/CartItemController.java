package com.ecommers.cart.controller;

import com.ecommers.cart.dto.CartItemDto.CartRequest;
import com.ecommers.cart.dto.CartItemDto.CartResponse;
import com.ecommers.cart.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService service;

    @PostMapping
    public ResponseEntity<EntityModel<CartResponse>> addToCart(@Valid @RequestBody CartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(service.addToCart(request)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<CartResponse>>> getCartByUser(@PathVariable Long userId) {
        List<EntityModel<CartResponse>> items = service.getCartByUser(userId).stream()
                .map(this::toModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(items,
                linkTo(methodOn(CartItemController.class).getCartByUser(userId)).withSelfRel()));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<EntityModel<CartResponse>> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        return ResponseEntity.ok(toModel(service.updateQuantity(id, quantity)));
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

    private EntityModel<CartResponse> toModel(CartResponse item) {
        return EntityModel.of(item,
                linkTo(methodOn(CartItemController.class).getCartByUser(item.userId())).withRel("user-cart"));
    }
}
