package models;

import java.util.List;
import java.time.LocalDateTime;

public class Order {
    private int id;
    private List<CartItem> items;
    private String address;
    private LocalDateTime createdAt;

    public Order(int id, List<CartItem> items, String address) {
        this.id = id;
        this.items = items;
        this.address = address;
        this.createdAt = LocalDateTime.now();
    }

    public double getTotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    @Override
    public String toString() {
        return "Заказ #" + id + " | " + createdAt + "\nСумма: " + getTotal() + "₸\nАдрес: " + address;
    }
}
