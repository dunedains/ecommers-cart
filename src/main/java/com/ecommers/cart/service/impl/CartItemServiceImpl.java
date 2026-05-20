package com.ecommers.cart.service.impl;

import com.ecommers.cart.client.InventoryClient;
import com.ecommers.cart.client.ProductClient;
import com.ecommers.cart.dto.CartItemDto.*;
import com.ecommers.cart.exception.CartItemNotFoundException;
import com.ecommers.cart.model.CartItem;
import com.ecommers.cart.repository.CartItemRepository;
import com.ecommers.cart.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository repository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public CartResponse addToCart(CartRequest request) {
        productClient.getProductById(request.productId());

        InventoryDto stock = inventoryClient.getStock(request.productId());
        if (stock.quantity() < request.quantity()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + stock.quantity());
        }

        CartItem item = repository.findByUserIdAndProductId(request.userId(), request.productId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUserId(request.userId());
                    newItem.setProductId(request.productId());
                    newItem.setQuantity(0);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + request.quantity());
        CartItem saved = repository.save(item);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> getCartByUser(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(Long id, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        CartItem item = repository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException(id));

        InventoryDto stock = inventoryClient.getStock(item.getProductId());
        if (stock.quantity() < quantity) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + stock.quantity());
        }

        item.setQuantity(quantity);
        return toResponse(repository.save(item));
    }

    @Override
    @Transactional
    public void removeFromCart(Long id) {
        if (!repository.existsById(id)) {
            throw new CartItemNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        repository.deleteByUserId(userId);
    }

    private CartResponse toResponse(CartItem item) {
        return new CartResponse(item.getId(), item.getUserId(), item.getProductId(), item.getQuantity());
    }
}
