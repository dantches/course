package com.onlinestore.controller;

import com.onlinestore.model.Category;
import com.onlinestore.model.Product;
import com.onlinestore.service.CategoryService;
import com.onlinestore.service.ImageStorageService;
import com.onlinestore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ImageStorageService imageStorageService;

    @GetMapping
    public String adminDashboard(Model model) {
        List<Product> products = productService.findAll();
        List<Category> categories = categoryService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "admin/dashboard";
    }

    // Category CRUD
    @GetMapping("/categories")
    public String categoriesList(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories";
    }

    @GetMapping("/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }

    @PostMapping("/categories")
    public String saveCategory(@RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            if (categoryService.existsByName(name)) {
                redirectAttributes.addFlashAttribute("error", "Category already exists");
                return "redirect:/admin/categories/new";
            }
            Category category = new Category(name);
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("success", "Category created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        model.addAttribute("category", category);
        return "admin/category-form";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @RequestParam String name,
                                 RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            category.setName(name);
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // Product CRUD
    @GetMapping("/products")
    public String productsList(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product-form";
    }

    @PostMapping("/products")
    public String saveProduct(@RequestParam String title,
                             @RequestParam BigDecimal price,
                             @RequestParam(required = false) Long categoryId,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) Integer stock,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) {
        try {
            Product product = new Product();
            product.setTitle(title);
            product.setPrice(price);
            if (categoryId != null) {
                Category category = categoryService.findById(categoryId)
                        .orElse(null);
                product.setCategory(category);
            }
            product.setDescription(description);
            product.setStock(stock);
            String imagePath = imageStorageService.storeProductImage(imageFile);
            if (imagePath != null) {
                product.setImageUrl(imagePath);
            }
            productService.save(product);
            redirectAttributes.addFlashAttribute("success", "Product created successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product-form";
    }

    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam BigDecimal price,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) Integer stock,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                               @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
                               RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setTitle(title);
            product.setPrice(price);
            if (categoryId != null) {
                Category category = categoryService.findById(categoryId)
                        .orElse(null);
                product.setCategory(category);
            }
            product.setDescription(description);
            product.setStock(stock);
            if (removeImage && product.getImageUrl() != null) {
                imageStorageService.deleteImage(product.getImageUrl());
                product.setImageUrl(null);
            }
            if (imageFile != null && !imageFile.isEmpty()) {
                imageStorageService.deleteImage(product.getImageUrl());
                String imagePath = imageStorageService.storeProductImage(imageFile);
                product.setImageUrl(imagePath);
            }
            productService.save(product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}




