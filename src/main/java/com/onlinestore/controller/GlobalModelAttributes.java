package com.onlinestore.controller;

import com.onlinestore.model.CartItem;
import com.onlinestore.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    private final CartService cartService;

    @Autowired
    public GlobalModelAttributes(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute
    public void addCartItemCount(Model model, HttpServletRequest request) {
        HttpSession session = request != null ? request.getSession(false) : null;
        @SuppressWarnings("unchecked")
        List<CartItem> cart = session != null ? (List<CartItem>) session.getAttribute("cart") : null;
        model.addAttribute("cartItemCount", cartService.getItemCount(cart));
    }
}


