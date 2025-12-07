package dao;

import config.DBConnection;
import models.Category;
import models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT p.id, p.title, p.price, c.id AS c_id, c.name AS c_name
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            ORDER BY p.id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Category category = null;
                int catId = rs.getInt("c_id");
                if (!rs.wasNull()) {
                    category = new Category(catId, rs.getString("c_name"));
                }

                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("price"),
                        category
                );
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // в курсовой можно красиво оформить логирование
        }
        return list;
    }

    @Override
    public Product findById(int id) {
        String sql = """
            SELECT p.id, p.title, p.price, c.id AS c_id, c.name AS c_name
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE p.id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Category category = null;
                    int catId = rs.getInt("c_id");
                    if (!rs.wasNull()) {
                        category = new Category(catId, rs.getString("c_name"));
                    }

                    return new Product(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getDouble("price"),
                            category
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
