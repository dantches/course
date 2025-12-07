package dao;

import config.DBConnection;
import models.CartItem;
import models.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    @Override
    public Order saveOrder(List<CartItem> items, String address) {
        String insertOrderSql = "INSERT INTO orders (address, created_at, total) VALUES (?, ?, ?)";
        String insertItemSql  = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // начинаем транзакцию

            double total = items.stream()
                    .mapToDouble(CartItem::getSubtotal)
                    .sum();

            LocalDateTime createdAt = LocalDateTime.now();

            // 1) Вставляем заказ
            orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setString(1, address);
            orderStmt.setTimestamp(2, Timestamp.valueOf(createdAt));
            orderStmt.setDouble(3, total);
            orderStmt.executeUpdate();

            int orderId;
            try (ResultSet keys = orderStmt.getGeneratedKeys()) {
                if (keys.next()) {
                    orderId = keys.getInt(1);
                } else {
                    throw new SQLException("Order ID not generated");
                }
            }

            // 2) Вставляем позиции заказа
            itemStmt = conn.prepareStatement(insertItemSql);
            for (CartItem ci : items) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, ci.getProduct().getId());
                itemStmt.setInt(3, 1); // тут можно сделать поле quantity в CartItem
                itemStmt.setDouble(4, ci.getProduct().getPrice());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();

            conn.commit();

            // создаём объект Order для возврата
            return new Order(orderId, items, address);

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (itemStmt != null) itemStmt.close();
                if (orderStmt != null) orderStmt.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
