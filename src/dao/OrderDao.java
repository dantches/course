package dao;

import models.CartItem;
import models.Order;

import java.util.List;

public interface OrderDao {
    Order saveOrder(List<CartItem> items, String address);
}
