package com.onlinestore.model;

import java.math.BigDecimal;

public class CartItem {
    private Product product;
    private Integer quantity;

    public CartItem() {
        this.quantity = 1;
    }

    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1;
    }

    public CartItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public void increment() {
        quantity++;
    }

    public void decrement() {
        if (quantity > 1) {
            quantity--;
        }
    }

    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}










