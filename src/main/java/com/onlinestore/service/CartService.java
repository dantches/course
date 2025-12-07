package com.onlinestore.service;

import com.onlinestore.model.CartItem;
import com.onlinestore.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    public List<CartItem> addItem(List<CartItem> cart, Product product, Integer quantity) {
        if (cart == null) {
            cart = new ArrayList<>();
        }

        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            cart.add(new CartItem(product, quantity));
        }

        return cart;
    }

    public List<CartItem> removeItem(List<CartItem> cart, Long productId) {
        if (cart == null) {
            return new ArrayList<>();
        }
        cart.removeIf(item -> item.getProduct().getId().equals(productId));
        return cart;
    }

    public List<CartItem> updateQuantity(List<CartItem> cart, Long productId, Integer quantity) {
        if (cart == null) {
            return new ArrayList<>();
        }
        cart.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        return cart;
    }

    public BigDecimal getTotal(List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount(List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            return 0;
        }
        return cart.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}










