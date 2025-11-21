package com.busbooking.system.controller;

import com.busbooking.system.model.UserRequest;
import com.busbooking.system.service.UserRequestService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRequestService requestService;

    // Available request types
    private final List<String> REQUEST_TYPES = Arrays.asList(
        "ACCOUNT_UPDATE", "REFUND_REQUEST", "COMPLAINT", "FEEDBACK", "OTHER"
    );

    // Show request form for users
    @GetMapping("/request-form")
    public String showRequestForm(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        model.addAttribute("requestTypes", REQUEST_TYPES);
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
            System.out.println("📝 Submitting request for user: " + username);
            
            UserRequest request = new UserRequest(username, requestType, description);
            UserRequest savedRequest = requestService.createRequest(request);
            
            System.out.println("✅ Request submitted successfully with ID: " + savedRequest.getId());
            
            return "redirect:/my-requests?username=" + username + "&message=Request submitted successfully!";
            
        } catch (Exception e) {
            System.out.println("❌ Failed to submit request: " + e.getMessage());
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
            
            System.out.println("✅ Loaded " + requests.size() + " requests for user: " + username);
            
            return "my-requests";
            
        } catch (Exception e) {
            System.out.println("❌ Error loading requests: " + e.getMessage());
            model.addAttribute("error", "Error loading requests: " + e.getMessage());
            return "my-requests";
        }
    }

    // NOTE: All admin endpoints (/admin/requests, /admin/update-request, /admin/delete-request) 
    // are now ONLY in AdminController to avoid conflicts
}