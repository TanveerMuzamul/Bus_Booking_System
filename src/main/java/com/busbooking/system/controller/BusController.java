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

@Controller
public class BusController {

    private static final Logger logger = LoggerFactory.getLogger(BusController.class);

    private static final String USERNAME_ATTRIBUTE = "username";
    private static final String BUSES_ATTRIBUTE = "buses";
    private static final String LOCATIONS_ATTRIBUTE = "locations";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String BUS_ATTRIBUTE = "bus";
    private static final String CART_ITEMS_ATTRIBUTE = "cartItems";
    private static final String TOTAL_ATTRIBUTE = "total";
    private static final String TRAVEL_DATE_ATTRIBUTE = "travelDate";
    private static final String BOOKINGS_ATTRIBUTE = "bookings";
    private static final String PAYMENT_METHOD_ATTRIBUTE = "paymentMethod";
    private static final String BOOKING_COUNT_ATTRIBUTE = "bookingCount";

    private static final String BUSES_VIEW = "buses";
    private static final String BOOKING_FORM_VIEW = "booking-form";
    private static final String CART_VIEW = "cart";
    private static final String PAYMENT_VIEW = "payment";
    private static final String BOOKING_SUCCESS_VIEW = "booking-success";
    private static final String MY_BOOKINGS_VIEW = "my-bookings";
    private static final String DASHBOARD_VIEW = "dashboard";

    private static final String REDIRECT_BUSES = "redirect:/buses";
    private static final String REDIRECT_CART = "redirect:/cart";
    private static final String REDIRECT_PAYMENT = "redirect:/payment";
    private static final String REDIRECT_BOOK = "redirect:/book/";

    private final List<String> irelandLocations = Arrays.asList(
            "Dublin", "Cork", "Galway", "Limerick", "Waterford", "Drogheda",
            "Dundalk", "Swords", "Bray", "Navan", "Kilkenny", "Ennis", "Carlow",
            "Tralee", "Newbridge", "Portlaoise", "Balbriggan", "Naas", "Athlone",
            "Mullingar", "Wexford", "Letterkenny", "Sligo", "Greystones", "Clonmel"
    );

    private final BusService busService;
    private final BookingService bookingService;
    private final CartService cartService;

    public BusController(BusService busService,
                         BookingService bookingService,
                         CartService cartService) {
        this.busService = busService;
        this.bookingService = bookingService;
        this.cartService = cartService;
    }

    @GetMapping("/dashboard")
    public String userDashboard(@RequestParam String username, Model model) {
        model.addAttribute(USERNAME_ATTRIBUTE, username);
        return DASHBOARD_VIEW;
    }

    @GetMapping("/buses")
    public String listBuses(@RequestParam(required = false) String source,
                            @RequestParam(required = false) String destination,
                            @RequestParam(required = false) String date,
                            @RequestParam String username,
                            Model model) {

        try {
            List<Bus> buses = (source != null && !source.isEmpty() &&
                               destination != null && !destination.isEmpty())
                    ? busService.searchBuses(source, destination, date)
                    : busService.getActiveBuses();

            model.addAttribute(BUSES_ATTRIBUTE, buses);
            model.addAttribute(USERNAME_ATTRIBUTE, username);
            model.addAttribute(LOCATIONS_ATTRIBUTE, irelandLocations);
            model.addAttribute("selectedSource", source);
            model.addAttribute("selectedDestination", destination);
            model.addAttribute("selectedDate", date);

            return BUSES_VIEW;

        } catch (Exception e) {
            logger.error("Error loading buses: {}", e.getMessage());
            model.addAttribute(ERROR_ATTRIBUTE, "Error loading buses");
            return BUSES_VIEW;
        }
    }

    @GetMapping("/book/{id}")
    public String showBookingForm(@PathVariable Long id,
                                  @RequestParam String username,
                                  @RequestParam(required = false) String travelDate,
                                  Model model) {

        Bus bus = busService.getBusById(id);
        if (bus == null) {
            return REDIRECT_BUSES + "?" + USERNAME_ATTRIBUTE + "=" + username;
        }

        if (travelDate == null || travelDate.isEmpty()) {
            travelDate = LocalDate.now().plusDays(1).toString();
        }

        model.addAttribute(BUS_ATTRIBUTE, bus);
        model.addAttribute(USERNAME_ATTRIBUTE, username);
        model.addAttribute(TRAVEL_DATE_ATTRIBUTE, travelDate);

        return BOOKING_FORM_VIEW;
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long busId,
                            @RequestParam String username,
                            @RequestParam String travelDate,
                            @RequestParam int passengers) {

        Bus bus = busService.getBusById(busId);
        if (bus == null) {
            return REDIRECT_BUSES + "?" + USERNAME_ATTRIBUTE + "=" + username;
        }

        if (!isValidTravelDate(travelDate)) {
            return REDIRECT_BOOK + busId + "?" + USERNAME_ATTRIBUTE + "=" + username;
        }

        Cart cart = new Cart(
                username,
                busId,
                bus.getBusName(),
                bus.getSource(),
                bus.getDestination(),
                travelDate,
                passengers,
                bus.getPrice() * passengers
        );

        cartService.addToCart(cart);
        return REDIRECT_CART + "?" + USERNAME_ATTRIBUTE + "=" + username;
    }

    private boolean isValidTravelDate(String travelDate) {
        try {
            LocalDate date = LocalDate.parse(travelDate);
            return !date.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/cart")
    public String viewCart(@RequestParam String username,
                           @RequestParam(required = false) String message,
                           @RequestParam(required = false) String error,
                           Model model) {

        List<Cart> cartItems = cartService.getCartByUser(username);
        double total = cartService.getCartTotal(username);

        model.addAttribute(CART_ITEMS_ATTRIBUTE, cartItems);
        model.addAttribute(TOTAL_ATTRIBUTE, total);
        model.addAttribute(USERNAME_ATTRIBUTE, username);
        model.addAttribute(MESSAGE_ATTRIBUTE, message);
        model.addAttribute(ERROR_ATTRIBUTE, error);

        return CART_VIEW;
    }

    @GetMapping("/remove-from-cart/{id}")
    public String removeFromCart(@PathVariable Long id,
                                 @RequestParam String username) {

        cartService.removeFromCart(id);
        return REDIRECT_CART + "?" + USERNAME_ATTRIBUTE + "=" + username;
    }

    @GetMapping("/payment")
    public String showPayment(@RequestParam String username, Model model) {

        List<Cart> cartItems = cartService.getCartByUser(username);
        if (cartItems.isEmpty()) {
            return REDIRECT_CART + "?" + USERNAME_ATTRIBUTE + "=" + username;
        }

        model.addAttribute(CART_ITEMS_ATTRIBUTE, cartItems);
        model.addAttribute(TOTAL_ATTRIBUTE, cartService.getCartTotal(username));
        model.addAttribute(USERNAME_ATTRIBUTE, username);

        return PAYMENT_VIEW;
    }

    @PostMapping("/process-payment")
    public String processPayment(@RequestParam String username,
                                 @RequestParam double total,
                                 @RequestParam String paymentMethod,
                                 Model model) {

        List<Cart> cartItems = cartService.getCartByUser(username);

        if (cartItems.isEmpty()) {
            return REDIRECT_CART + "?" + USERNAME_ATTRIBUTE + "=" + username;
        }

        for (Cart cart : cartItems) {

            String dateStr = cart.getTravelDate();
            if (dateStr == null || dateStr.isEmpty()) {
                dateStr = LocalDate.now().plusDays(1).toString();
            }

            LocalDate travelDate;
            try {
                travelDate = LocalDate.parse(dateStr.trim());
            } catch (Exception e) {
                logger.error("Invalid travel date format: {}", dateStr);
                continue;
            }

            Booking booking = new Booking(
                    username,
                    cart.getBusName(),
                    cart.getSource(),
                    cart.getDestination(),
                    travelDate,
                    cart.getPassengers(),
                    cart.getTotalPrice()
            );

            bookingService.saveBooking(booking);
        }

        cartService.clearCart(username);

        model.addAttribute(USERNAME_ATTRIBUTE, username);
        model.addAttribute(TOTAL_ATTRIBUTE, total);
        model.addAttribute(PAYMENT_METHOD_ATTRIBUTE, paymentMethod);
        model.addAttribute(BOOKING_COUNT_ATTRIBUTE, cartItems.size());
        model.addAttribute(BOOKINGS_ATTRIBUTE, cartItems);

        return BOOKING_SUCCESS_VIEW;
    }

    @GetMapping("/my-bookings")
    public String myBookings(@RequestParam String username,
                             @RequestParam(required = false) String message,
                             Model model) {

        List<Booking> bookings = bookingService.getBookingsByUser(username);

        model.addAttribute(BOOKINGS_ATTRIBUTE, bookings);
        model.addAttribute(USERNAME_ATTRIBUTE, username);
        model.addAttribute(MESSAGE_ATTRIBUTE, message);

        return MY_BOOKINGS_VIEW;
    }
}
