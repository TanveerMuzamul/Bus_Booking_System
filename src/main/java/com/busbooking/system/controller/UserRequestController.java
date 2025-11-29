package com.busbooking.system.controller;

import com.busbooking.system.model.UserRequest;
import com.busbooking.system.service.UserRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for handling user requests - ONLY USER ENDPOINTS
 * Admin endpoints are in AdminController
 */
@Controller
public class UserRequestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRequestController.class);

    // Constructor injection instead of field injection
    private final UserRequestService requestService;

    // Available request types - fixed naming convention
    private final List<String> requestTypes = Arrays.asList(
        "ACCOUNT_UPDATE", "REFUND_REQUEST", "COMPLAINT", "FEEDBACK", "OTHER"
    );

    public UserRequestController(UserRequestService requestService) {
        this.requestService = requestService;
    }

    // Show request form for users
    @GetMapping("/request-form")
    public String showRequestForm(@RequestParam String username, Model model) {
        logger.info("Loading request form for user");
        model.addAttribute("username", username);
        model.addAttribute("requestTypes", requestTypes);
        model.addAttribute("userRequest", new UserRequest());
        return "request-form";
    }

    // Submit new request
    @PostMapping("/submit-request")
    public String submitRequest(@RequestParam String username,
                               @RequestParam String requestType,
                               @RequestParam String description,
                               Model model) {
        try {
            logger.info("Submitting user request");
            
            UserRequest request = new UserRequest(username, requestType, description);
            UserRequest savedRequest = requestService.createRequest(request);
            
            logger.info("Request submitted successfully with ID: {}", savedRequest.getId());
            
            return "redirect:/my-requests?username=" + username + "&message=Request submitted successfully!";
            
        } catch (Exception e) {
            logger.error("Failed to submit request: {}", e.getMessage());
            return "redirect:/request-form?username=" + username + "&error=Failed to submit request: " + e.getMessage();
        }
    }

    // View user's requests
    @GetMapping("/my-requests")
    public String myRequests(@RequestParam String username,
                            @RequestParam(required = false) String message,
                            @RequestParam(required = false) String error,
                            Model model) {
        try {
            List<UserRequest> requests = requestService.getRequestsByUser(username);
            
            model.addAttribute("requests", requests);
            model.addAttribute("username", username);
            if (message != null) model.addAttribute("message", message);
            if (error != null) model.addAttribute("error", error);
            
            logger.info("Loaded {} requests for user", requests.size());
            
            return "my-requests";
            
        } catch (Exception e) {
            logger.error("Error loading requests: {}", e.getMessage());
            model.addAttribute("error", "Error loading requests: " + e.getMessage());
            return "my-requests";
        }
    }

    // NOTE: All admin endpoints (/admin/requests, /admin/update-request, /admin/delete-request) 
    // are now ONLY in AdminController to avoid conflicts
}