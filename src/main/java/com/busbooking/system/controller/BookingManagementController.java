package com.busbooking.system.controller;

import com.busbooking.system.model.Booking;
import com.busbooking.system.model.Bus;
import com.busbooking.system.service.BookingService;
import com.busbooking.system.service.BusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for admin booking management
 * Allows admins to view, edit, update, and cancel user bookings
 */
@Controller
@RequestMapping("/admin")
public class BookingManagementController {

    private static final Logger logger = LoggerFactory.getLogger(BookingManagementController.class);

    // Constructor injection instead of field injection
    private final BookingService bookingService;
    private final BusService busService;

    public BookingManagementController(BookingService bookingService, BusService busService) {
        this.bookingService = bookingService;
        this.busService = busService;
    }

    /**
     * View all bookings for management
     */
    @GetMapping("/bookings")
    public String viewAllBookings(Model model) {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            model.addAttribute("bookings", bookings);
            logger.info("Loaded {} bookings for management", bookings.size());
            return "admin-bookings";
        } catch (Exception e) {
            logger.error("Error loading bookings: {}", e.getMessage());
            model.addAttribute("error", "Error loading bookings: " + e.getMessage());
            return "admin-bookings";
        }
    }

    /**
     * Show edit booking form
     */
    @GetMapping("/edit-booking/{id}")
    public String editBookingForm(@PathVariable Long id, Model model) {
        try {
            // For now, we'll use a workaround since getBookingById might not exist
            List<Booking> allBookings = bookingService.getAllBookings();
            Booking booking = allBookings.stream()
                    .filter(b -> b.getId().equals(id))
                    .findFirst()
                    .orElse(null);
                    
            if (booking == null) {
                model.addAttribute("error", "Booking not found!");
                return "redirect:/admin/bookings";
            }
            
            List<Bus> buses = busService.getAllBuses();
            model.addAttribute("booking", booking);
            model.addAttribute("buses", buses);
            logger.info("Loaded booking for editing: {}", booking.getId());
            return "admin-edit-booking";
        } catch (Exception e) {
            logger.error("Error loading booking: {}", e.getMessage());
            return "redirect:/admin/bookings?error=Error loading booking";
        }
    }

    /**
     * Update booking details - FIXED: Proper parameter binding
     */
    @PostMapping("/update-booking")
    public String updateBooking(@RequestParam("bookingId") Long bookingId,
                               @RequestParam("busId") Long busId,
                               @RequestParam("travelDate") String travelDate,
                               @RequestParam("passengers") int passengers,
                               @RequestParam("status") String status,
                               Model model) {
        try {
            logger.info("Starting booking update");
            
            // Workaround to find booking
            List<Booking> allBookings = bookingService.getAllBookings();
            Booking booking = allBookings.stream()
                    .filter(b -> b.getId().equals(bookingId))
                    .findFirst()
                    .orElse(null);
                    
            if (booking == null) {
                logger.error("Booking not found with ID: {}", bookingId);
                return "redirect:/admin/bookings?error=Booking not found!";
            }

            Bus bus = busService.getBusById(busId);
            if (bus != null) {
                booking.setBusName(bus.getBusName());
                booking.setSource(bus.getSource());
                booking.setDestination(bus.getDestination());
                logger.info("Bus updated: {}", bus.getBusName());
            } else {
                logger.warn("Bus not found with ID: {}", busId);
            }

            booking.setTravelDate(LocalDate.parse(travelDate));
            booking.setPassengers(passengers);
            
            // Calculate total price if bus is found
            if (bus != null) {
                double totalPrice = bus.getPrice() * passengers;
                booking.setTotalPrice(totalPrice);
                logger.info("Total price calculated: €{}", totalPrice);
            }
            
            booking.setStatus(status);

            // Save the updated booking
            Booking updatedBooking = bookingService.saveBooking(booking);
            logger.info("Booking updated successfully: {}", updatedBooking.getId());
            
            return "redirect:/admin/bookings?success=Booking updated successfully!";
            
        } catch (Exception e) {
            logger.error("Error updating booking: {}", e.getMessage());
            return "redirect:/admin/bookings?error=Error updating booking: " + e.getMessage();
        }
    }
}