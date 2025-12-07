package models;

public class Product {
    private int id;
    private String title;
    private double price;
    private Category category;
    private String imageUrl;

    public Product() {}

    public Product(int id, String title, double price, Category category) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.category = category;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public Category getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(Category category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        String catName = (category != null) ? category.getName() : "Без категории";
        return id + ". " + title + " - " + price + "₸ (" + catName + ")";
    }
}
