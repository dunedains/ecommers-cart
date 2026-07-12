package com.ecommers.cart.controller;

import com.ecommers.cart.dto.CartItemDto.CartRequest;
import com.ecommers.cart.dto.CartItemDto.CartResponse;
import com.ecommers.cart.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "Carrito de compras con validación de stock contra inventario (Feign)")
public class CartItemController {

    private final CartItemService service;

    @PostMapping
    @Operation(summary = "Agregar un producto al carrito",
            description = "Valida que el producto exista en el catálogo y que haya stock suficiente en inventario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto agregado al carrito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "El producto no existe")
    })
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addToCart(request));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Ver el carrito de un usuario")
    @ApiResponse(responseCode = "200", description = "Items del carrito (puede estar vacío)")
    public ResponseEntity<List<CartResponse>> getCartByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getCartByUser(userId));
    }

    @PatchMapping("/{id}/quantity")
    @Operation(summary = "Cambiar la cantidad de un item del carrito")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada"),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "El item no existe")
    })
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long id,
            @Parameter(description = "Nueva cantidad (mínimo 1)") @RequestParam Integer quantity) {
        return ResponseEntity.ok(service.updateQuantity(id, quantity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Quitar un item del carrito")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item eliminado"),
            @ApiResponse(responseCode = "404", description = "El item no existe")
    })
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        service.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Vaciar el carrito de un usuario")
    @ApiResponse(responseCode = "204", description = "Carrito vaciado")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        service.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
