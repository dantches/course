package com.onlinestore.controller;

import com.onlinestore.model.CartItem;
import com.onlinestore.model.Product;
import com.onlinestore.service.CartService;
import com.onlinestore.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.getTotal(cart));
        model.addAttribute("title", "Your Toy Land Cart");
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") Integer quantity,
                           @RequestParam(required = false) String returnUrl,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (quantity == null || quantity < 1) {
            quantity = 1;
        }

        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        cart = cartService.addItem(cart, product, quantity);
        session.setAttribute("cart", cart);

        redirectAttributes.addFlashAttribute("success", product.getTitle() + " is now in your cart! 🧸");
        return "redirect:" + resolveReturnUrl(returnUrl);
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam Long productId,
                            @RequestParam Integer quantity,
                            HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        cart = cartService.updateQuantity(cart, productId, quantity);
        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        cart = cartService.removeItem(cart, productId);
        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }
    private String resolveReturnUrl(String returnUrl) {
        if (StringUtils.hasText(returnUrl) && returnUrl.startsWith("/") && !returnUrl.contains("://")) {
            return returnUrl;
        }
        return "/cart";
    }
}




