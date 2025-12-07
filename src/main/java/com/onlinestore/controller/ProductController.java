package com.onlinestore.controller;

import com.onlinestore.model.Product;
import com.onlinestore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        List<Product> suggestions = productService.findFeaturedProducts(6).stream()
                .filter(p -> !p.getId().equals(product.getId()))
                .limit(3)
                .collect(Collectors.toList());

        model.addAttribute("product", product);
        model.addAttribute("suggestedProducts", suggestions);
        model.addAttribute("title", product.getTitle() + " | Toy Land");

        return "product-details";
    }
}


