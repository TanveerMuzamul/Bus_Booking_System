package com.busbooking.system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Long busId;
    private String busName;
    private String source;
    private String destination;
    private String travelDate;
    private int passengers;
    private double totalPrice;

    public Cart() {}

    public Cart(String username, Long busId, String busName) {
        this(username, busId, busName, "Dublin", "Galway",
                java.time.LocalDate.now().plusDays(1).toString(), 1, 25.0);
    }

    public Cart(String username, Long busId, String busName, String source,
                String destination, String travelDate, int passengers, double totalPrice) {
        this.username = username;
        this.busId = busId;
        this.busName = busName;
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.passengers = passengers;
        this.totalPrice = totalPrice;
    }

    // ******** GETTERS & SETTERS ********

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public Long getBusId() { return busId; }

    public void setBusId(Long busId) { this.busId = busId; }

    public String getBusName() { return busName; }

    public void setBusName(String busName) { this.busName = busName; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }

    public void setDestination(String destination) { this.destination = destination; }

    public String getTravelDate() { return travelDate; }

    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }

    public int getPassengers() { return passengers; }

    public void setPassengers(int passengers) { this.passengers = passengers; }

    public double getTotalPrice() { return totalPrice; }

    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
