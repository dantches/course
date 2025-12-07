package com.onlinestore.controller;

import com.onlinestore.model.CartItem;
import com.onlinestore.model.Order;
import com.onlinestore.model.User;
import com.onlinestore.service.CartService;
import com.onlinestore.service.OrderService;
import com.onlinestore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.getTotal(cart));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam String address,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cart is empty");
            return "redirect:/cart";
        }

        try {
            Order order = orderService.createOrder(user, address, cart);
            session.removeAttribute("cart");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating order: " + e.getMessage());
            return "redirect:/orders/checkout";
        }
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "order-receipt";
    }

    @GetMapping
    public String myOrders(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        List<Order> orders = orderService.findByUser(user);
        model.addAttribute("orders", orders);
        return "my-orders";
    }
}

