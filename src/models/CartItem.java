package models;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1;
    }

    public void increment() { quantity++; }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    public Product getProduct() { return product; }

    @Override
    public String toString() {
        return product.getTitle() + " x" + quantity + " = " + getSubtotal() + "₸";
    }
}
