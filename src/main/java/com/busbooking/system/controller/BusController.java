package com.busbooking.system.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BusService busService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CartService cartService;

    // Ireland locations
    private final List<String> IRELAND_LOCATIONS = Arrays.asList(
        "Dublin", "Cork", "Galway", "Limerick", "Waterford", "Drogheda", 
        "Dundalk", "Swords", "Bray", "Navan", "Kilkenny", "Ennis", "Carlow",
        "Tralee", "Newbridge", "Portlaoise", "Balbriggan", "Naas", "Athlone",
        "Mullingar", "Wexford", "Letterkenny", "Sligo", "Greystones", "Clonmel"
    );

    // User Dashboard - FIXED: Added this method
    @GetMapping("/dashboard")
    public String userDashboard(@RequestParam String username, Model model) {
        System.out.println("🏠 Loading user dashboard for: " + username);
        model.addAttribute("username", username);
        return "dashboard";
    }

    // Show all buses with search - FIXED: Better error handling
    @GetMapping("/buses")
    public String listBuses(@RequestParam(required = false) String source,
                           @RequestParam(required = false) String destination,
                           @RequestParam(required = false) String date,
                           @RequestParam String username,
                           Model model) {
        
        System.out.println("🚌 Loading buses page for user: " + username);
        
        try {
            List<Bus> buses;
            if (source != null && !source.isEmpty() && destination != null && !destination.isEmpty()) {
                System.out.println("🔍 Performing search...");
                buses = busService.searchBuses(source, destination, date);
            } else {
                System.out.println("📋 Loading all active buses...");
                buses = busService.getActiveBuses();
            }

            System.out.println("✅ Displaying " + buses.size() + " buses");
            
            model.addAttribute("buses", buses);
            model.addAttribute("username", username);
            model.addAttribute("locations", IRELAND_LOCATIONS);
            model.addAttribute("selectedSource", source);
            model.addAttribute("selectedDestination", destination);
            model.addAttribute("selectedDate", date);
            
            return "buses";
            
        } catch (Exception e) {
            System.out.println("❌ Error loading buses: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading buses: " + e.getMessage());
            return "buses";
        }
    }

    // Show booking form - FIXED: Better validation
    @GetMapping("/book/{id}")
    public String showBookingForm(@PathVariable Long id,
                                 @RequestParam String username,
                                 @RequestParam(required = false) String travelDate,
                                 Model model) {
        try {
            System.out.println("🎫 Loading booking form for bus ID: " + id);
            
            Bus bus = busService.getBusById(id);
            if (bus == null) {
                System.out.println("❌ Bus not found with ID: " + id);
                model.addAttribute("error", "Bus not found!");
                return "redirect:/buses?username=" + username;
            }

            // Set default travel date to tomorrow if not provided
            if (travelDate == null || travelDate.isEmpty()) {
                travelDate = LocalDate.now().plusDays(1).toString();
            }

            System.out.println("✅ Loading booking form for: " + bus.getBusName());
            
            model.addAttribute("bus", bus);
            model.addAttribute("username", username);
            model.addAttribute("travelDate", travelDate);
            return "booking-form";
            
        } catch (Exception e) {
            System.out.println("❌ Error loading booking form: " + e.getMessage());
            model.addAttribute("error", "Error loading booking form: " + e.getMessage());
            return "redirect:/buses?username=" + username;
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
            System.out.println("🛒 Adding to cart - Bus ID: " + busId + ", User: " + username);
            
            Bus bus = busService.getBusById(busId);
            if (bus == null) {
                System.out.println("❌ Bus not found with ID: " + busId);
                model.addAttribute("error", "Bus not found!");
                return "redirect:/buses?username=" + username;
            }

            // Validate travel date
            try {
                LocalDate travelLocalDate = LocalDate.parse(travelDate);
                if (travelLocalDate.isBefore(LocalDate.now())) {
                    model.addAttribute("error", "Travel date cannot be in the past!");
                    return "redirect:/book/" + busId + "?username=" + username + "&travelDate=" + travelDate;
                }
            } catch (Exception e) {
                model.addAttribute("error", "Invalid travel date!");
                return "redirect:/book/" + busId + "?username=" + username;
            }

            // Create cart item
            Cart cart = new Cart(username, busId, bus.getBusName(), bus.getSource(),
                    bus.getDestination(), travelDate, passengers,
                    bus.getPrice() * passengers);

            cartService.addToCart(cart);
            System.out.println("✅ Added to cart successfully");
            
            return "redirect:/cart?username=" + username + "&message=Added to cart successfully!";
            
        } catch (Exception e) {
            System.out.println("❌ Error adding to cart: " + e.getMessage());
            model.addAttribute("error", "Error adding to cart: " + e.getMessage());
            return "redirect:/buses?username=" + username;
        }
    }

    // View cart
    @GetMapping("/cart")
    public String viewCart(@RequestParam String username, 
                          @RequestParam(required = false) String message,
                          @RequestParam(required = false) String error,
                          Model model) {
        System.out.println("🛒 Loading cart for user: " + username);
        
        List<Cart> cartItems = cartService.getCartByUser(username);
        double total = cartService.getCartTotal(username);
        
        System.out.println("✅ Cart has " + cartItems.size() + " items, total: €" + total);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("username", username);
        if (message != null) model.addAttribute("message", message);
        if (error != null) model.addAttribute("error", error);
        return "cart";
    }

    // Remove from cart
    @GetMapping("/remove-from-cart/{id}")
    public String removeFromCart(@PathVariable Long id, @RequestParam String username) {
        System.out.println("🗑️ Removing cart item ID: " + id);
        cartService.removeFromCart(id);
        return "redirect:/cart?username=" + username + "&message=Item removed from cart!";
    }

    // Show payment page
    @GetMapping("/payment")
    public String showPayment(@RequestParam String username, Model model) {
        System.out.println("💳 Loading payment page for user: " + username);
        
        List<Cart> cartItems = cartService.getCartByUser(username);
        double total = cartService.getCartTotal(username);
        
        if (cartItems.isEmpty()) {
            System.out.println("❌ Cart is empty, redirecting...");
            return "redirect:/cart?username=" + username + "&error=Your cart is empty!";
        }
        
        System.out.println("✅ Processing payment for " + cartItems.size() + " items");
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("username", username);
        return "payment";
    }

    // Process payment
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam String username,
                               @RequestParam double total,
                               @RequestParam String paymentMethod,
                               HttpServletRequest request,
                               Model model) {
        try {
            System.out.println("💳 Starting payment processing for: " + username);
            
            // Get cart items
            List<Cart> cartItems = cartService.getCartByUser(username);
            
            if (cartItems.isEmpty()) {
                System.out.println("❌ Cart is empty");
                return "redirect:/cart?username=" + username + "&error=Your cart is empty!";
            }
            
            // Validate that we have cart items
            System.out.println("📦 Processing " + cartItems.size() + " cart items");
            
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
                System.out.println("✅ Booking created: " + cart.getBusName() + " for " + cart.getTravelDate());
            }
            
            // Clear cart after successful payment
            cartService.clearCart(username);
            System.out.println("✅ Cart cleared successfully");
            
            // Log successful payment
            System.out.println("💰 Payment Successful!");
            System.out.println("👤 User: " + username);
            System.out.println("💶 Amount: €" + total);
            System.out.println("💳 Method: " + paymentMethod);
            System.out.println("🎫 Tickets: " + cartItems.size());
            
            // Add success attributes
            model.addAttribute("username", username);
            model.addAttribute("total", total);
            model.addAttribute("paymentMethod", paymentMethod);
            model.addAttribute("bookingCount", cartItems.size());
            model.addAttribute("bookings", cartItems);
            
            System.out.println("✅ Payment completed successfully, redirecting to success page");
            return "booking-success";
            
        } catch (Exception e) {
            System.out.println("❌ Payment processing error: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/payment?username=" + username + "&error=Payment processing failed. Please try again.";
        }
    }

    // My Bookings - FIXED: Proper implementation
    @GetMapping("/my-bookings")
    public String myBookings(@RequestParam String username,
                            @RequestParam(required = false) String message,
                            Model model) {
        try {
            System.out.println("📖 Loading bookings for user: " + username);
            
            List<Booking> bookings = bookingService.getBookingsByUser(username);
            System.out.println("✅ Found " + bookings.size() + " bookings for user: " + username);
            
            model.addAttribute("bookings", bookings);
            model.addAttribute("username", username);
            if (message != null) model.addAttribute("message", message);
            return "my-bookings";
            
        } catch (Exception e) {
            System.out.println("❌ Error loading bookings: " + e.getMessage());
            model.addAttribute("error", "Error loading bookings: " + e.getMessage());
            return "redirect:/buses?username=" + username;
        }
    }
}