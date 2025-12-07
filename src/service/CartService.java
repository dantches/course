package service;

import models.CartItem;
import models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private final List<CartItem> cart = new ArrayList<>();

    public void add(Product product) {
        for (CartItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                item.increment();
                return;
            }
        }
        cart.add(new CartItem(product));
    }

    public void showCart() {
        if (cart.isEmpty()) {
            System.out.println("Корзина пуста.");
            return;
        }
        cart.forEach(System.out::println);
        System.out.println("Итого: " + getTotal() + "₸");
    }

    public List<CartItem> getItems() { return cart; }

    public double getTotal() {
        return cart.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }

    public void clear() {
        cart.clear();
    }
}
