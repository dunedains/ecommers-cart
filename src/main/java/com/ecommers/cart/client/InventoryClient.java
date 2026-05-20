package com.ecommers.cart.client;

import com.ecommers.cart.dto.CartItemDto.InventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", url = "${feign.client.inventory-url}")
public interface InventoryClient {

    @GetMapping("/api/inventory/{productId}")
    InventoryDto getStock(@PathVariable Long productId);
}