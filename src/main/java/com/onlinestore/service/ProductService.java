package com.onlinestore.service;

import com.onlinestore.model.Product;
import com.onlinestore.model.Category;
import com.onlinestore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchByTitle(String title) {
        return productRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Product> findFeaturedProducts(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return productRepository.findRandomProducts(limit);
    }
}




