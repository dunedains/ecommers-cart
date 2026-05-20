package com.ecommers.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemDto {

    public record CartRequest(
            @NotNull Long userId,
            @NotNull Long productId,
            @NotNull @Min(1) Integer quantity) {
    }

    public record CartResponse(
            Long id,
            Long userId,
            Long productId,
            Integer quantity) {
    }

    public record ProductDto(
            Long id,
            String name,
            Double price) {
    }

    public record InventoryDto(
            Long id,
            Long productId,
            Integer quantity) {
    }
}
