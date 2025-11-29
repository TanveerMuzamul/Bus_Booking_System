package com.busbooking.system.repository;

import com.busbooking.system.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find all bookings by username
    List<Booking> findByUsername(String username);
}