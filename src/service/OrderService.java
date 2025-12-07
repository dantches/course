package service;

import dao.OrderDao;
import models.CartItem;
import models.Order;

import java.util.List;

public class OrderService {
    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public Order createAndSaveOrder(List<CartItem> items, String address) {
        return orderDao.saveOrder(items, address);
    }
}
