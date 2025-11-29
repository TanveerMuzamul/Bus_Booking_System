package com.busbooking.system.controller;

import com.busbooking.system.model.Bus;
import com.busbooking.system.model.User;
import com.busbooking.system.service.BusService;
import com.busbooking.system.service.UserRequestService;
import com.busbooking.system.service.UserService;
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

    // String constants for duplicate literals
    private static final String LOCATIONS_ATTRIBUTE = "locations";
    private static final String BUS_TYPES_ATTRIBUTE = "busTypes";
    private static final String DAYS_OF_WEEK_ATTRIBUTE = "daysOfWeek";
    private static final String ADMIN_BUS_FORM_VIEW = "admin-bus-form";
    private static final String SUCCESS_MESSAGE = "success";
    private static final String REDIRECT_ADMIN_DASHBOARD = "redirect:/admin/dashboard";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String USER_ROLES_ATTRIBUTE = "userRoles";
    private static final String REDIRECT_ADMIN_USERS = "redirect:/admin/users";

    // Ireland locations - fixed naming convention
    private final List<String> irelandLocations = Arrays.asList(
        "Dublin", "Cork", "Galway", "Limerick", "Waterford", "Drogheda", 
        "Dundalk", "Swords", "Bray", "Navan", "Kilkenny", "Ennis", "Carlow",
        "Tralee", "Newbridge", "Portlaoise", "Balbriggan", "Naas", "Athlone",
        "Mullingar", "Wexford", "Letterkenny", "Sligo", "Greystones", "Clonmel"
    );

    private final List<String> busTypes = Arrays.asList("STANDARD", "EXPRESS", "PREMIUM");
    private final List<String> daysOfWeek = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
    private final List<String> userRoles = Arrays.asList("USER", "ADMIN");

    // Constructor injection instead of field injection
    private final BusService busService;
    private final UserService userService;
    private final UserRequestService userRequestService;

    public AdminController(BusService busService, UserService userService, UserRequestService userRequestService) {
        this.busService = busService;
        this.userService = userService;
        this.userRequestService = userRequestService;
    }

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
        model.addAttribute(LOCATIONS_ATTRIBUTE, irelandLocations);
        model.addAttribute(BUS_TYPES_ATTRIBUTE, busTypes);
        model.addAttribute(DAYS_OF_WEEK_ATTRIBUTE, daysOfWeek);
        return ADMIN_BUS_FORM_VIEW;
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
            model.addAttribute(SUCCESS_MESSAGE, "Bus saved successfully!");
            return REDIRECT_ADMIN_DASHBOARD;
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, "Error saving bus: " + e.getMessage());
            model.addAttribute("bus", bus);
            model.addAttribute(LOCATIONS_ATTRIBUTE, irelandLocations);
            model.addAttribute(BUS_TYPES_ATTRIBUTE, busTypes);
            model.addAttribute(DAYS_OF_WEEK_ATTRIBUTE, daysOfWeek);
            return ADMIN_BUS_FORM_VIEW;
        }
    }

    // Delete bus
    @GetMapping("/delete-bus/{id}")
    public String deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return REDIRECT_ADMIN_DASHBOARD;
    }

    // Edit bus - FIXED: Proper editing
    @GetMapping("/edit-bus/{id}")
    public String editBus(@PathVariable Long id, Model model) {
        try {
            Bus bus = busService.getBusById(id);
            if (bus == null) {
                model.addAttribute(ERROR_ATTRIBUTE, "Bus not found!");
                return REDIRECT_ADMIN_DASHBOARD;
            }
            model.addAttribute("bus", bus);
            model.addAttribute(LOCATIONS_ATTRIBUTE, irelandLocations);
            model.addAttribute(BUS_TYPES_ATTRIBUTE, busTypes);
            model.addAttribute(DAYS_OF_WEEK_ATTRIBUTE, daysOfWeek);
            return ADMIN_BUS_FORM_VIEW;
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, "Error loading bus: " + e.getMessage());
            return REDIRECT_ADMIN_DASHBOARD;
        }
    }

    // ==================== USER MANAGEMENT ====================

    // User Management Dashboard - FIXED: Show actual users
    @GetMapping("/users")
    public String userDashboard(Model model) {
        // Get all users from service
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute(USER_ROLES_ATTRIBUTE, userRoles);
        return "admin-users";
    }

    // Edit user form
    @GetMapping("/edit-user/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                model.addAttribute(ERROR_ATTRIBUTE, "User not found!");
                return REDIRECT_ADMIN_USERS;
            }
            model.addAttribute("user", user);
            model.addAttribute(USER_ROLES_ATTRIBUTE, userRoles);
            return "admin-user-form";
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, "Error loading user: " + e.getMessage());
            return REDIRECT_ADMIN_USERS;
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
            model.addAttribute(SUCCESS_MESSAGE, "User updated successfully!");
            return REDIRECT_ADMIN_USERS;
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, "Error saving user: " + e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute(USER_ROLES_ATTRIBUTE, userRoles);
            return "admin-user-form";
        }
    }

    // Delete user
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return REDIRECT_ADMIN_USERS;
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
            model.addAttribute(SUCCESS_MESSAGE, "Request updated successfully!");
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, "Failed to update request: " + e.getMessage());
        }
        return "redirect:/admin/requests";
    }
}