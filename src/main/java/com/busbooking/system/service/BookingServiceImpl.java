package com.busbooking.system.service;

import com.busbooking.system.model.Booking;
import com.busbooking.system.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Booking saveBooking(Booking booking) {
        try {
            logger.info("Saving booking");
            return bookingRepository.save(booking);
        } catch (RuntimeException e) {
            logger.error("Error saving booking: {}", e.getMessage());
            throw new RuntimeException("Failed to save booking", e);
        }
    }

    @Override
    public List<Booking> getBookingsByUser(String username) {
        logger.info("Fetching bookings for user");
        return bookingRepository.findByUsername(username);
    }

    @Override
    public List<Booking> getAllBookings() {
        logger.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    @Override
    public Booking getBookingById(Long id) {
        if (id == null) {
            logger.error("Attempted to fetch booking with null ID");
            return null;
        }
        logger.info("Fetching booking with ID: {}", id);
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteBooking(Long id) {
        if (id == null) {
            logger.error("Attempted to delete booking with null ID");
            return;
        }
        logger.info("Deleting booking with ID: {}", id);
        try {
            bookingRepository.deleteById(id);
        } catch (RuntimeException e) {
            logger.error("Error deleting booking: {}", e.getMessage());
            throw new RuntimeException("Failed to delete booking", e);
        }
    }
}
