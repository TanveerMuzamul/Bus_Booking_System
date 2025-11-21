package com.busbooking.system.controller;

import com.busbooking.system.model.Bus;
import com.busbooking.system.model.User;
import com.busbooking.system.service.BusService;
import com.busbooking.system.service.UserRequestService;
import com.busbooking.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Admin Controller - Handles all admin-related operations
 * Manages buses, users, and user requests
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BusService busService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRequestService userRequestService; // Added for request management

    // Ireland locations
    private final List<String> IRELAND_LOCATIONS = Arrays.asList(
        "Dublin", "Cork", "Galway", "Limerick", "Waterford", "Drogheda", 
        "Dundalk", "Swords", "Bray", "Navan", "Kilkenny", "Ennis", "Carlow",
        "Tralee", "Newbridge", "Portlaoise", "Balbriggan", "Naas", "Athlone",
        "Mullingar", "Wexford", "Letterkenny", "Sligo", "Greystones", "Clonmel"
    );

    private final List<String> BUS_TYPES = Arrays.asList("STANDARD", "EXPRESS", "PREMIUM");
    private final List<String> DAYS_OF_WEEK = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
    private final List<String> USER_ROLES = Arrays.asList("USER", "ADMIN");

    // ==================== BUS MANAGEMENT ====================

    // Bus Dashboard - UPDATED: Added pending requests count
    @GetMapping("/dashboard")
    public String busDashboard(Model model) {
        model.addAttribute("buses", busService.getAllBuses());
        model.addAttribute("pendingRequestsCount", userRequestService.getPendingRequestsCount());
        return "admin-dashboard";
    }

    // Add bus form
    @GetMapping("/add-bus")
    public String addBusForm(Model model) {
        model.addAttribute("bus", new Bus());
        model.addAttribute("locations", IRELAND_LOCATIONS);
        model.addAttribute("busTypes", BUS_TYPES);
        model.addAttribute("daysOfWeek", DAYS_OF_WEEK);
        return "admin-bus-form";
    }

    // Save bus - FIXED: Proper form handling
    @PostMapping("/save-bus")
    public String saveBus(@ModelAttribute Bus bus,
                         @RequestParam String departureTime,
                         @RequestParam String arrivalTime,
                         @RequestParam String startDate,
                         @RequestParam String endDate,
                         @RequestParam(required = false) List<String> operatingDays,
                         Model model) {
        
        try {
            // Convert time strings to LocalTime
            bus.setDepartureTime(LocalTime.parse(departureTime));
            bus.setArrivalTime(LocalTime.parse(arrivalTime));
            
            // Convert date strings to LocalDate
            bus.setStartDate(LocalDate.parse(startDate));
            bus.setEndDate(LocalDate.parse(endDate));
            
            // Convert operating days list to comma-separated string
            if (operatingDays != null && !operatingDays.isEmpty()) {
                bus.setOperatingDays(String.join(",", operatingDays));
            } else {
                bus.setOperatingDays("MON,TUE,WED,THU,FRI,SAT,SUN");
            }
            
            busService.saveBus(bus);
            model.addAttribute("success", "Bus saved successfully!");
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving bus: " + e.getMessage());
            model.addAttribute("bus", bus);
            model.addAttribute("locations", IRELAND_LOCATIONS);
            model.addAttribute("busTypes", BUS_TYPES);
            model.addAttribute("daysOfWeek", DAYS_OF_WEEK);
            return "admin-bus-form";
        }
    }

    // Delete bus
    @GetMapping("/delete-bus/{id}")
    public String deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return "redirect:/admin/dashboard";
    }

    // Edit bus - FIXED: Proper editing
    @GetMapping("/edit-bus/{id}")
    public String editBus(@PathVariable Long id, Model model) {
        try {
            Bus bus = busService.getBusById(id);
            if (bus == null) {
                model.addAttribute("error", "Bus not found!");
                return "redirect:/admin/dashboard";
            }
            model.addAttribute("bus", bus);
            model.addAttribute("locations", IRELAND_LOCATIONS);
            model.addAttribute("busTypes", BUS_TYPES);
            model.addAttribute("daysOfWeek", DAYS_OF_WEEK);
            return "admin-bus-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading bus: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    // ==================== USER MANAGEMENT ====================

    // User Management Dashboard - FIXED: Show actual users
    @GetMapping("/users")
    public String userDashboard(Model model) {
        // Get all users from service
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("userRoles", USER_ROLES);
        return "admin-users";
    }

    // Edit user form
    @GetMapping("/edit-user/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                model.addAttribute("error", "User not found!");
                return "redirect:/admin/users";
            }
            model.addAttribute("user", user);
            model.addAttribute("userRoles", USER_ROLES);
            return "admin-user-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading user: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }

    // Save user - UPDATED: Handle phone number
    @PostMapping("/save-user")
    public String saveUser(@ModelAttribute User user, Model model) {
        try {
            // For existing users, preserve the password if not changed
            if (user.getId() != null) {
                User existingUser = userService.getUserById(user.getId());
                if (existingUser != null && (user.getPassword() == null || user.getPassword().isEmpty())) {
                    user.setPassword(existingUser.getPassword());
                }
            }
            
            userService.saveUser(user);
            model.addAttribute("success", "User updated successfully!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving user: " + e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("userRoles", USER_ROLES);
            return "admin-user-form";
        }
    }

    // Delete user
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    // ==================== REQUEST MANAGEMENT ====================

    // Admin: View all requests
    @GetMapping("/requests")
    public String viewAllRequests(Model model) {
        List<com.busbooking.system.model.UserRequest> requests = userRequestService.getAllRequests();
        long pendingCount = userRequestService.getPendingRequestsCount();
        
        model.addAttribute("requests", requests);
        model.addAttribute("pendingCount", pendingCount);
        return "admin-requests";
    }

    // Admin: Update request status
    @PostMapping("/update-request")
    public String updateRequestStatus(@RequestParam Long requestId,
                                     @RequestParam String status,
                                     @RequestParam String adminResponse,
                                     Model model) {
        try {
            userRequestService.updateRequestStatus(requestId, status, adminResponse);
            model.addAttribute("success", "Request updated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update request: " + e.getMessage());
        }
        return "redirect:/admin/requests";
    }
}