package com.busbooking.system.controller;

import com.busbooking.system.model.Booking;
import com.busbooking.system.model.Bus;
import com.busbooking.system.service.BookingService;
import com.busbooking.system.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BusService busService;

    /**
     * View all bookings for management
     */
    @GetMapping("/bookings")
    public String viewAllBookings(Model model) {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            model.addAttribute("bookings", bookings);
            System.out.println("✅ Loaded " + bookings.size() + " bookings for management");
            return "admin-bookings";
        } catch (Exception e) {
            System.out.println("❌ Error loading bookings: " + e.getMessage());
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
            System.out.println("✅ Loaded booking for editing: " + booking.getId());
            return "admin-edit-booking";
        } catch (Exception e) {
            System.out.println("❌ Error loading booking: " + e.getMessage());
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
            System.out.println("🔄 Starting booking update...");
            System.out.println("📝 Parameters - Booking ID: " + bookingId + ", Bus ID: " + busId + 
                             ", Travel Date: " + travelDate + ", Passengers: " + passengers + 
                             ", Status: " + status);
            
            // Workaround to find booking
            List<Booking> allBookings = bookingService.getAllBookings();
            Booking booking = allBookings.stream()
                    .filter(b -> b.getId().equals(bookingId))
                    .findFirst()
                    .orElse(null);
                    
            if (booking == null) {
                System.out.println("❌ Booking not found with ID: " + bookingId);
                return "redirect:/admin/bookings?error=Booking not found!";
            }

            Bus bus = busService.getBusById(busId);
            if (bus != null) {
                booking.setBusName(bus.getBusName());
                booking.setSource(bus.getSource());
                booking.setDestination(bus.getDestination());
                System.out.println("✅ Bus updated: " + bus.getBusName());
            } else {
                System.out.println("⚠️ Bus not found with ID: " + busId);
            }

            booking.setTravelDate(LocalDate.parse(travelDate));
            booking.setPassengers(passengers);
            
            // Calculate total price if bus is found
            if (bus != null) {
                double totalPrice = bus.getPrice() * passengers;
                booking.setTotalPrice(totalPrice);
                System.out.println("💰 Total price calculated: €" + totalPrice);
            }
            
            booking.setStatus(status);

            // Save the updated booking
            Booking updatedBooking = bookingService.saveBooking(booking);
            System.out.println("✅ Booking updated successfully: " + updatedBooking.getId());
            
            return "redirect:/admin/bookings?success=Booking updated successfully!";
            
        } catch (Exception e) {
            System.out.println("❌ Error updating booking: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/bookings?error=Error updating booking: " + e.getMessage();
        }
    }
}