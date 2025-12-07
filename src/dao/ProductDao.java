package dao;

import models.Product;
import java.util.List;

public interface ProductDao {
    List<Product> findAll();
    Product findById(int id);
}
