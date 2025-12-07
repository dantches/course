package com.onlinestore.controller;

import com.onlinestore.dto.UserRegistrationDto;
import com.onlinestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

@Controller
public class AuthController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Autowired
    private UserService userService;

    @ModelAttribute("registrationForm")
    public UserRegistrationDto registrationForm() {
        return new UserRegistrationDto();
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registrationForm") UserRegistrationDto registrationDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        validateRegistrationInput(registrationDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(registrationDto);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            String message = ex.getMessage();
            if (message != null) {
                String lowerMessage = message.toLowerCase();
                if (lowerMessage.contains("username")) {
                    bindingResult.rejectValue("username", "duplicate", message);
                } else if (lowerMessage.contains("email")) {
                    bindingResult.rejectValue("email", "duplicate", message);
                } else {
                    bindingResult.reject("registration.error", message);
                }
            } else {
                bindingResult.reject("registration.error", "Unable to register user.");
            }
            return "register";
        } catch (RuntimeException ex) {
            bindingResult.reject("registration.error", "Unable to register user.");
            return "register";
        }
    }

    private void validateRegistrationInput(UserRegistrationDto registrationDto, BindingResult bindingResult) {
        if (!StringUtils.hasText(registrationDto.getUsername())) {
            bindingResult.rejectValue("username", "NotBlank", "Username is required");
        } else {
            String trimmedUsername = registrationDto.getUsername().trim();
            if (trimmedUsername.length() < 3 || trimmedUsername.length() > 30) {
                bindingResult.rejectValue("username", "Size", "Username must be between 3 and 30 characters");
            }
            registrationDto.setUsername(trimmedUsername);
        }

        if (!StringUtils.hasText(registrationDto.getEmail())) {
            bindingResult.rejectValue("email", "NotBlank", "Email is required");
        } else {
            String trimmedEmail = registrationDto.getEmail().trim();
            if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
                bindingResult.rejectValue("email", "Email", "Invalid email address");
            }
            registrationDto.setEmail(trimmedEmail);
        }

        if (!StringUtils.hasText(registrationDto.getPassword())) {
            bindingResult.rejectValue("password", "NotBlank", "Password is required");
        } else if (registrationDto.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "Size", "Password must be at least 6 characters");
        }

        if (!StringUtils.hasText(registrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "NotBlank", "Confirm password is required");
        }

        if (StringUtils.hasText(registrationDto.getPassword())
                && StringUtils.hasText(registrationDto.getConfirmPassword())
                && !registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
        }
    }
}

