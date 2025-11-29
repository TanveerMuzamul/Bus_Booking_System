package com.busbooking.system.controller;

import com.busbooking.system.model.User;
import com.busbooking.system.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller - Handles user login, registration, and authentication
 */
@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    // Constants for duplicate string literals
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String USERNAME_PARAM = "username";
    private static final String REDIRECT_DASHBOARD = "redirect:/dashboard?";
    private static final String REDIRECT_LOGIN = "redirect:/login?";

    // Constructor injection instead of field injection
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 🏠 Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // 🔑 Show login page
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute(ERROR_ATTRIBUTE, null);
        return "login";
    }

    // 🔐 Handle login - FIXED: Redirect to user dashboard
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {

        logger.info("Login attempt");

        // 🔐 Admin login check
        if ("admin".equals(username) && "admin123".equals(password)) {
            logger.info("Admin login successful");
            return "redirect:/admin/dashboard";
        }

        // 🧾 Validate user credentials from DB
        boolean isValid = userService.validateUser(username, password);
        logger.info("User validation result: {}", isValid);

        if (isValid) {
            logger.info("User login successful - Redirecting to user dashboard");
            return REDIRECT_DASHBOARD + USERNAME_PARAM + "=" + username; // Redirect to user dashboard
        }

        // ❌ Invalid credentials
        logger.info("Login failed - Invalid credentials");
        model.addAttribute(ERROR_ATTRIBUTE, "Invalid username or password");
        return "login";
    }

    // 🧾 Registration page
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // 🧍‍♀️ Handle registration - FIXED: Better error handling
    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            logger.info("Registration attempt");
            
            userService.register(user);
            logger.info("Registration successful");
            
            return REDIRECT_LOGIN + "success=Registration successful! Please login.";
            
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            model.addAttribute(ERROR_ATTRIBUTE, "Registration failed: " + e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }
}