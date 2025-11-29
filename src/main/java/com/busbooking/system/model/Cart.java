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

    // Builder pattern to avoid too many constructor parameters
    public static class CartBuilder {
        private String username;
        private Long busId;
        private String busName;
        private String source = "Dublin";
        private String destination = "Galway";
        private String travelDate = java.time.LocalDate.now().plusDays(1).toString();
        private int passengers = 1;
        private double totalPrice = 25.0;

        public CartBuilder(String username, Long busId, String busName) {
            this.username = username;
            this.busId = busId;
            this.busName = busName;
        }

        public CartBuilder source(String source) {
            this.source = source;
            return this;
        }

        public CartBuilder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public CartBuilder travelDate(String travelDate) {
            this.travelDate = travelDate;
            return this;
        }

        public CartBuilder passengers(int passengers) {
            this.passengers = passengers;
            return this;
        }

        public CartBuilder totalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Cart build() {
            Cart cart = new Cart();
            cart.username = this.username;
            cart.busId = this.busId;
            cart.busName = this.busName;
            cart.source = this.source;
            cart.destination = this.destination;
            cart.travelDate = this.travelDate;
            cart.passengers = this.passengers;
            cart.totalPrice = this.totalPrice;
            return cart;
        }
    }

    // Static factory method for builder
    public static CartBuilder builder(String username, Long busId, String busName) {
        return new CartBuilder(username, busId, busName);
    }

    // Getters and Setters
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