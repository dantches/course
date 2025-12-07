package com.onlinestore.repository;

import com.onlinestore.model.Product;
import com.onlinestore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByTitleContainingIgnoreCase(String title);

    @Query(value = "SELECT * FROM products ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Product> findRandomProducts(@Param("limit") int limit);
}




