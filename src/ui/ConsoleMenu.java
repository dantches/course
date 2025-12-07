package ui;

import dao.ProductDao;
import dao.ProductDaoImpl;
import dao.OrderDaoImpl;
import models.Product;
import service.CartService;
import service.OrderService;

import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final ProductDao productDao = new ProductDaoImpl();
    private final CartService cart = new CartService();
    private final OrderService orderService = new OrderService(new OrderDaoImpl());

    public void start() {
        while (true) {
            System.out.println("\n--- Магазин игрушек ---");
            System.out.println("1. Каталог");
            System.out.println("2. Корзина");
            System.out.println("3. Оформить заказ");
            System.out.println("0. Выход");

            System.out.print("Выбор: ");
            int choice = readInt();

            switch (choice) {
                case 1 -> showCatalog();
                case 2 -> cart.showCart();
                case 3 -> createOrder();
                case 0 -> {
                    System.out.println("Выход...");
                    return;
                }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private void showCatalog() {
        System.out.println("\n--- Каталог ---");
        var products = productDao.findAll();
        if (products.isEmpty()) {
            System.out.println("Нет товаров в базе.");
            return;
        }
        products.forEach(System.out::println);

        System.out.print("Введите ID товара для добавления в корзину (0 - назад): ");
        int id = readInt();
        if (id == 0) return;

        Product p = productDao.findById(id);
        if (p != null) {
            cart.add(p);
            System.out.println("Товар добавлен в корзину.");
        } else {
            System.out.println("Товара с таким ID нет.");
        }
    }

    private void createOrder() {
        if (cart.isEmpty()) {
            System.out.println("Корзина пуста, оформлять нечего.");
            return;
        }

        cart.showCart();
        System.out.print("Введите адрес доставки: ");
        scanner.nextLine(); // съедаем перевод строки
        String address = scanner.nextLine();

        var order = orderService.createAndSaveOrder(cart.getItems(), address);
        if (order != null) {
            System.out.println("\nЗаказ успешно оформлен и сохранён в БД:");
            System.out.println(order);
            cart.clear();
        } else {
            System.out.println("Ошибка при сохранении заказа.");
        }
    }

    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Введите число: ");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
