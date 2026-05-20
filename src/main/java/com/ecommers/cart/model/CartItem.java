package com.ecommers.cart.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cart_item")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Integer quantity;
    private Long userId;
}
