package com.busbooking.system.service;

import com.busbooking.system.model.Booking;
import com.busbooking.system.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking testBooking1;
    private Booking testBooking2;

    @BeforeEach
    void setUp() {
        testBooking1 = new Booking();
        testBooking1.setId(1L);
        testBooking1.setUsername("user1");
        testBooking1.setBusName("CityLink Express");
        testBooking1.setSource("Dublin");
        testBooking1.setDestination("Galway");
        testBooking1.setTravelDate(LocalDate.now().plusDays(1));
        testBooking1.setPassengers(2);
        testBooking1.setTotalPrice(51.00);
        testBooking1.setStatus("CONFIRMED");
        testBooking1.setBookingDate(LocalDateTime.now());

        testBooking2 = new Booking();
        testBooking2.setId(2L);
        testBooking2.setUsername("user2");
        testBooking2.setBusName("CityLink Premium");
        testBooking2.setSource("Dublin");
        testBooking2.setDestination("Cork");
        testBooking2.setTravelDate(LocalDate.now().plusDays(2));
        testBooking2.setPassengers(1);
        testBooking2.setTotalPrice(29.99);
        testBooking2.setStatus("PENDING");
        testBooking2.setBookingDate(LocalDateTime.now());
    }

    @Test
    void testSaveBooking() {
        // Arrange
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking1);

        // Act
        Booking savedBooking = bookingService.saveBooking(testBooking1);

        // Assert
        assertNotNull(savedBooking);
        assertEquals("CityLink Express", savedBooking.getBusName());
        assertEquals("user1", savedBooking.getUsername());
        verify(bookingRepository, times(1)).save(testBooking1);
    }

    @Test
    void testGetBookingsByUser() {
        // Arrange
        when(bookingRepository.findByUsername("user1")).thenReturn(Arrays.asList(testBooking1));

        // Act
        List<Booking> bookings = bookingService.getBookingsByUser("user1");

        // Assert
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("user1", bookings.get(0).getUsername());
        verify(bookingRepository, times(1)).findByUsername("user1");
    }

    @Test
    void testGetAllBookings() {
        // Arrange
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(testBooking1, testBooking2));

        // Act
        List<Booking> bookings = bookingService.getAllBookings();

        // Assert
        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testGetBookingById() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1));


        Booking foundBooking = bookingService.getBookingById(1L);

        // Assert
        assertNotNull(foundBooking);
        assertEquals(1L, foundBooking.getId());
        assertEquals("CityLink Express", foundBooking.getBusName());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookingByIdNotFound() {
        // Arrange
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Booking foundBooking = bookingService.getBookingById(99L);

        // Assert
        assertNull(foundBooking);
        verify(bookingRepository, times(1)).findById(99L);
    }

    @Test
    void testDeleteBooking() {
        // Arrange
        doNothing().when(bookingRepository).deleteById(1L);

        // Act
        bookingService.deleteBooking(1L);

        // Assert
        verify(bookingRepository, times(1)).deleteById(1L);
    }
}