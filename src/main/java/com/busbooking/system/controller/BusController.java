package com.busbooking.system.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.busbooking.system.model.Booking;
import com.busbooking.system.model.Bus;
import com.busbooking.system.model.Cart;
import com.busbooking.system.service.BookingService;
import com.busbooking.system.service.BusService;
import com.busbooking.system.service.CartService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Bus Controller - Handles all bus-related operations for users
 * Manages bus viewing, booking, cart, and payments
 */
@Controller
public class BusController {

    private static final Logger logger = LoggerFactory.getLogger(BusController.class);
    
    // String constants
    private static final String USERNAME_ATTRIBUTE = "username";
    private static final String BUSES_ATTRIBUTE = "buses";
    private static final String LOCATIONS_ATTRIBUTE = "locations";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String BUS_ATTRIBUTE = "bus";
    private static final String CART_ITEMS_ATTRIBUTE = "cartItems";
    private static final String TOTAL_ATTRIBUTE = "total";
    private static final String TRAVEL_DATE_ATTRIBUTE = "travelDate";
    private static final String SELECTED_SOURCE_ATTRIBUTE = "selectedSource";
    private static final String SELECTED_DESTINATION_ATTRIBUTE = "selectedDestination";
    private static final String SELECTED_DATE_ATTRIBUTE = "selectedDate";
    private static final String BOOKINGS_ATTRIBUTE = "bookings";
    private static final String PAYMENT_METHOD_ATTRIBUTE = "paymentMethod";
    private static final String BOOKING_COUNT_ATTRIBUTE = "bookingCount";
    
    // View names
    private static final String BUSES_VIEW = "buses";
    private static final String BOOKING_FORM_VIEW = "booking-form";
    private static final String CART_VIEW = "cart";
    private static final String PAYMENT_VIEW = "payment";
    private static final String BOOKING_SUCCESS_VIEW = "booking-success";
    private static final String MY_BOOKINGS_VIEW = "my-bookings";
    private static final String DASHBOARD_VIEW = "dashboard";
    
    // Redirect URLs
    private static final String REDIRECT_BUSES = "redirect:/buses";
    private static final String REDIRECT_CART = "redirect:/cart";
    private static final String REDIRECT_PAYMENT = "redirect:/payment";
    private static final String REDIRECT_BOOK = "redirect:/book/";
    
    // Ireland locations
    private final List<String> irelandLocations = Arrays.asList(
        "Dublin", "Cork", "Galway", "Limerick", "Waterford", "Drogheda", 
        "Dundalk", "Swords", "Bray", "Navan", "Kilkenny", "Ennis", "Carlow",
        "Tralee", "Newbridge", "Portlaoise", "Balbriggan", "Naas", "Athlone",
        "Mullingar", "Wexford", "Letterkenny", "Sligo", "Greystones", "Clonmel"
    );

    private final BusService busService;
    private final BookingService bookingService;
    private final CartService cartService;

    public BusController(BusService busService, BookingService bookingService, CartService cartService) {
        this.busService = busService;
        this.bookingService = bookingService;
        this.cartService = cartService;
    }

    // User Dashboard - FIXED: Added this method
    @GetMapping("/dashboard")
    public String userDashboard(@RequestParam String username, Model model) {
        logger.info("Loading user dashboard for: {}", username);
        model.addAttribute(USERNAME_ATTRIBUTE, username);
        return DASHBOARD_VIEW;
    }

    // Show all buses with search - FIXED: Better error handling
    @GetMapping("/buses")
    public String listBuses(@RequestParam(required = false) String source,
                           @RequestParam(required = false) String destination,
                           @RequestParam(required = false) String date,
                           @RequestParam String username,
                           Model model) {
        
        logger.info("Loading buses page for user: {}", username);
        
        try {
            List<Bus> buses;
            if (source != null && !source.isEmpty() && destination != null && !destination.isEmpty()) {
                logger.info("Performing search from {} to {} on {}", source, destination, date);
                buses = busService.searchBuses(source, destination, date);
            } else {
                logger.info("Loading all active buses");
                buses = busService.getActiveBuses();
            }

            logger.info("Displaying {} buses", buses.size());
            
            model.addAttribute(BUSES_ATTRIBUTE, buses);
            model.addAttribute(USERNAME_ATTRIBUTE, username);
            model.addAttribute(LOCATIONS_ATTRIBUTE, irelandLocations);
            model.addAttribute(SELECTED_SOURCE_ATTRIBUTE, source);
            model.addAttribute(SELECTED_DESTINATION_ATTRIBUTE, destination);
            model.addAttribute(SELECTED_DATE_ATTRIBUTE, date);
            
            return BUSES_VIEW;
            
        } catch (Exception e) {
            logger.error("Error loading buses: {}", e.getMessage(), e);
            model.addAttribute(ERROR_ATTRIBUTE, "Error loading buses: " + e.getMessage());
            return BUSES_VIEW;
        }
    }

    // Show booking form - FIXED: Better validation
    @GetMapping("/book/{id}")
    public String showBookingForm(@PathVariable Long id,
                                 @RequestParam String username,
                                 @RequestParam(required = false) String travelDate,
                                 Model model) {
        try {
            logger.info("Loading booking form for bus ID: {}", id);
            
            Bus bus = busService.getBusById(id);
            if (bus == null) {
                logger.error("Bus not found with ID: {}", id);
                model.addAttribute(ERROR_ATTRIBUTE, "Bus not found!");
                return REDIRECT_BUSES + "?" + USERNAME_ATTRIBUTE + "=" + username;
            }

            // Set default travel date to tomorrow if not provided
            if (travelDate == null || travelDate.isEmpty()) {
                travelDate = LocalDate.now().plusDays(1).toString();
            }

            logger.info("Loading booking form for: {}", bus.getBusName());
            
            model.addAttribute(BUS_ATTRIBUTE, bus);
            model.addAttribute(USERNAME_ATTRIBUTE, username);
            model.addAttribute(TRAVEL_DATE_ATTRIBUTE, travelDate);
            return BOOKING_FORM_VIEW;
            
        } catch (Exception e) {
            logger.error("Error loading booking form for bus ID {}: {}", id, e.getMessage());
            model.addAttribute(ERROR_ATTRIBUTE, "Error loading booking form: " + e.getMessage());
            return REDIRECT_BUSES + "?" + USERNAME_ATTRIBUTE + "=" + username;
        }
    }

    // Add to cart - FIXED: Better validation
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long busId,
                           @RequestParam String username,
                           @RequestParam String travelDate,
                           @RequestParam int passengers,
                           Model model) {
        try {
            logger.info("Adding to cart - Bus ID: {}, User: {}", busId, username);
            
            Bus bus = busService.getBusById(busId);
            if (bus == null) {
                logger.error("Bus not found with ID: {}", busId);
                model.addAttribute(ERROR_ATTRIBUTE, "Bus not found!");
                return REDIRECT_BUSES + "?" + USERNAME_ATTRIBUTE + "=" + username;
            }

            // Validate travel date
            try {
                LocalDate travelLocalDate = LocalDate.parse(travelDate);
                if (travelLocalDate.isBefore(LocalDate.now())) {
                    model.addAttribute(ERROR_ATTRIBUTE, "Travel date cannot be in the past!");
                    return REDIRECT_BOOK + busId + "?" + USERNAME_ATTRIBUTE + "=" + username + "&travelDate=" + travelDate;
                }
            } catch (Exception e) {
                model.addAttribute(ERROR_ATTRIBUTE, "Invalid travel date!");
                return REDIRECT_BOOK + busId + "?" + USERNAME_ATTRIBUTE + "=" + username;
            }

            // Create cart item
            Cart cart = new Cart(username, busId, bus.getBusName(), bus.getSource(),
                    bus.getDestination(), travelDate, passengers,
                    bus.getPrice() * passengers);

            cartService.addToCart(cart);
            logger.info("Added to cart successfully for user: {}", username);
            
            return REDIRECT_CART + "?" + USERNAME_ATTRIBUTE + "=" + username + "&message=Added to cart successfully!";
            
        } catch (Exception e) {
            logger.error("Error adding to cart for user {}: {}", username, e.getMessage());
            model.addAttribute(ERROR_ATTRIBUTE, "Error adding to cart: " + e.getMessage());
            return REDIRECT_BUSES + "?" + USERNAME_ATTRIBUTE + "=" + username;
        }
    }

    // View cart
    @GetMapping("/cart")
    public String viewCart(@RequestParam String username, 
                          @RequestParam(required = false) String message,
                          @RequestParam(required = false) String error,
                          Model model) {
        logger.info("Loading cart for user: {}", username);
        
        List<Cart> cartItems = cartService.getCartByUser(username);
        double total = cartService.getCartTotal(username);
        
        logger.info("Cart has {} items, total: €{}", cartItems.size(), total);
        
        model.addAttribute(CART_ITEMS_ATTRIBUTE, cartItems);
        model.addAttribute(TOTAL_ATTRIBUTE, total);
        model.addAttribute(USERNAME_ATTRIBUTE, username);
        if (message != null) model.addAttribute(MESSAGE_ATTRIBUTE, message);
        if (error != null) model.addAttribute(ERROR_ATTRIBUTE, error);
        return CART_VIEW;
    }

    // Remove from cart
    @GetMapping("/remove-from-cart/{id}")
    public String removeFromCart(@PathVariable Long id, @RequestParam String username) {
        logger.info("Removing cart item ID: {} for user: {}", id, username);
        cartService.removeFromCart(id);
        return REDIRECT_CART + "?" + USERNAME_ATTRIBUTE + "=" + username + "&message=Item removed from cart!";
    }

    // Show payment page
    @GetMapping("/payment")
    public String showPayment(@RequestParam String username, Model model) {
        logger.info("Loading payment page for user: {}", username);
        
        List<Cart> cartItems = cartService.getCartByUser(username);
        double total = cartService.getCartTotal(username);
        
        if (cartItems.isEmpty()) {
            logger.warn("Cart is empty for user: {}", username);
            return REDIRECT_CART + "?" + USERNAME_ATTRIBUTE + "=" + username + "&error=Your cart is empty!";
        }
        
        logger.info("Processing payment for {} items", cartItems.size());
        
        model.addAttribute(CART_ITEMS_ATTRIBUTE, cartItems);
        model.addAttribute(TOTAL_ATTRIBUTE, total);
        model.addAttribute(USERNAME_ATTRIBUTE, username);
        return PAYMENT_VIEW;
    }

    // Process payment
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam String username,
                               @RequestParam double total,
                               @RequestParam String paymentMethod,
                               HttpServletRequest request,
                               Model model) {
        try {
            logger.info("Starting payment processing for: {}", username);
            
            // Get cart items
            List<Cart> cartItems = cartService.getCartByUser(username);
            
            if (cartItems.isEmpty()) {
                logger.error("Cart is empty for user: {}", username);
                return REDIRECT_CART + "?" + USERNAME_ATTRIBUTE + "=" + username + "&error=Your cart is empty!";
            }
            
            // Validate that we have cart items
            logger.info("Processing {} cart items", cartItems.size());
            
            // Create bookings for all cart items
            for (Cart cart : cartItems) {
                Booking booking = new Booking(
                    username,
                    cart.getBusName(),
                    cart.getSource(),
                    cart.getDestination(),
                    LocalDate.parse(cart.getTravelDate()),
                    cart.getPassengers(),
                    cart.getTotalPrice()
                );
                bookingService.saveBooking(booking);
                logger.info("Booking created: {} for {}", cart.getBusName(), cart.getTravelDate());
            }
            
            // Clear cart after successful payment
            cartService.clearCart(username);
            logger.info("Cart cleared successfully for user: {}", username);
            
            // Log successful payment
            logger.info("Payment Successful - User: {}, Amount: €{}, Method: {}, Tickets: {}", 
                       username, total, paymentMethod, cartItems.size());
            
            // Add success attributes
            model.addAttribute(USERNAME_ATTRIBUTE, username);
            model.addAttribute(TOTAL_ATTRIBUTE, total);
            model.addAttribute(PAYMENT_METHOD_ATTRIBUTE, paymentMethod);
            model.addAttribute(BOOKING_COUNT_ATTRIBUTE, cartItems.size());
            model.addAttribute(BOOKINGS_ATTRIBUTE, cartItems);
            
            logger.info("Payment completed successfully, redirecting to success page");
            return BOOKING_SUCCESS_VIEW;
            
        } catch (Exception e) {
            logger.error("Payment processing error for user {}: {}", username, e.getMessage(), e);
            return REDIRECT_PAYMENT + "?" + USERNAME_ATTRIBUTE + "=" + username + "&error=Payment processing failed. Please try again.";
        }
    }

    // My Bookings - FIXED: Proper implementation
    @GetMapping("/my-bookings")
    public String myBookings(@RequestParam String username,
                            @RequestParam(required = false) String message,
                            Model model) {
        try {
            logger.info("Loading bookings for user: {}", username);
            
            List<Booking> bookings = bookingService.getBookingsByUser(username);
            logger.info("Found {} bookings for user: {}", bookings.size(), username);
            
            model.addAttribute(BOOKINGS_ATTRIBUTE, bookings);
            model.addAttribute(USERNAME_ATTRIBUTE, username);
            if (message != null) model.addAttribute(MESSAGE_ATTRIBUTE, message);
            return MY_BOOKINGS_VIEW;
            
        } catch (Exception e) {
            logger.error("Error loading bookings for user {}: {}", username, e.getMessage());
            model.addAttribute(ERROR_ATTRIBUTE, "Error loading bookings: " + e.getMessage());
            return REDIRECT_BUSES + "?" + USERNAME_ATTRIBUTE + "=" + username;
        }
    }
}