package com.busbooking.system.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
public class Booking {

    // Constants for status literals
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String PAYMENT_STATUS_PAID = "PAID";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String busName;
    private String source;
    private String destination;
    private LocalDate travelDate;
    private LocalDateTime bookingDate;
    private int passengers;
    private double totalPrice;
    private String status = STATUS_CONFIRMED;
    private String paymentStatus = PAYMENT_STATUS_PAID;

    @Column(length = 1000)
    private String adminNotes;

    public Booking() {
        // default constructor for JPA
    }

    // Main constructor used in BusController during payment
    public Booking(String username,
                   String busName,
                   String source,
                   String destination,
                   LocalDate travelDate,
                   int passengers,
                   double totalPrice) {

        this.username = username;
        this.busName = busName;
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.bookingDate = LocalDateTime.now();
        this.passengers = passengers;
        this.totalPrice = totalPrice;
        this.status = STATUS_CONFIRMED;
        this.paymentStatus = PAYMENT_STATUS_PAID;
    }

    // Secondary constructor (kept for backward compatibility)
    public Booking(String username,
                   String busName,
                   String source,
                   String destination,
                   String bookingDate) {

        this.username = username;
        this.busName = busName;
        this.source = source;
        this.destination = destination;
        this.travelDate = LocalDate.parse(bookingDate);
        this.bookingDate = LocalDateTime.now();
        this.passengers = 1;
        this.totalPrice = 0.0;
        this.status = STATUS_CONFIRMED;
        this.paymentStatus = PAYMENT_STATUS_PAID;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}
