package com.busbooking.system.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.time.LocalDate;

@Entity
@Table(name = "bus")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String busName;
    private String source;
    private String destination;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private int seats;
    private double price;
    private String busType = "STANDARD";
    private boolean active = true;
    
    // New field for bus schedule dates
    private LocalDate startDate;
    private LocalDate endDate;
    private String operatingDays; // e.g., "MON,TUE,WED,THU,FRI,SAT,SUN"

    // Builder pattern to avoid too many constructor parameters
    public static class BusBuilder {
        private String busName;
        private String source;
        private String destination;
        private LocalTime departureTime = LocalTime.of(9, 0);
        private LocalTime arrivalTime = LocalTime.of(12, 0);
        private int seats = 50;
        private double price = 25.0;
        private String busType = "STANDARD";

        public BusBuilder(String busName, String source, String destination) {
            this.busName = busName;
            this.source = source;
            this.destination = destination;
        }

        public BusBuilder departureTime(LocalTime departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        public BusBuilder arrivalTime(LocalTime arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        public BusBuilder seats(int seats) {
            this.seats = seats;
            return this;
        }

        public BusBuilder price(double price) {
            this.price = price;
            return this;
        }

        public BusBuilder busType(String busType) {
            this.busType = busType;
            return this;
        }

        public Bus build() {
            Bus bus = new Bus();
            bus.busName = this.busName;
            bus.source = this.source;
            bus.destination = this.destination;
            bus.departureTime = this.departureTime;
            bus.arrivalTime = this.arrivalTime;
            bus.seats = this.seats;
            bus.price = this.price;
            bus.busType = this.busType;
            bus.startDate = LocalDate.now();
            bus.endDate = LocalDate.now().plusYears(1);
            bus.operatingDays = "MON,TUE,WED,THU,FRI,SAT,SUN";
            return bus;
        }
    }

    // Static factory method for builder
    public static BusBuilder builder(String busName, String source, String destination) {
        return new BusBuilder(busName, source, destination);
    }

    // Existing constructor for backward compatibility - use builder instead
    private Bus() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getOperatingDays() { return operatingDays; }
    public void setOperatingDays(String operatingDays) { this.operatingDays = operatingDays; }
}