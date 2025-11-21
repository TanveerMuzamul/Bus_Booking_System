package com.busbooking.system.service;

import com.busbooking.system.model.Booking;
import java.util.List;

public interface BookingService {
    Booking saveBooking(Booking booking);
    List<Booking> getBookingsByUser(String username);
    List<Booking> getAllBookings();
    
    // Add these missing methods:
    Booking getBookingById(Long id);
    void deleteBooking(Long id);
}