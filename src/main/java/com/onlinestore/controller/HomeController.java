package com.onlinestore.controller;

import com.onlinestore.model.Category;
import com.onlinestore.model.Product;
import com.onlinestore.service.CategoryService;
import com.onlinestore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String home(@RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String search,
                       Model model) {
        List<Product> products;
        List<Category> categories = categoryService.findAll();

        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchByTitle(search);
            model.addAttribute("search", search);
        } else if (categoryId != null) {
            products = productService.findByCategoryId(categoryId);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            products = productService.findAll();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("featuredProducts", productService.findFeaturedProducts(4));
        model.addAttribute("title", "Toy Land 🧸 | Playful toys for happy kids");

        return "index";
    }
}




