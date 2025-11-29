package com.busbooking.system.service;

import com.busbooking.system.model.Bus;
import java.util.List;

/**
 * Service interface for bus management operations
 * Defines business logic for bus-related functionality
 */
public interface BusService {
    
    /**
     * Get all buses from the database
     * @return List of all buses
     */
    List<Bus> getAllBuses();
    
    /**
     * Get only active buses
     * @return List of active buses
     */
    List<Bus> getActiveBuses();
    
    /**
     * Search buses by source, destination, and date
     * @param source Departure location
     * @param destination Arrival location
     * @param date Travel date (optional)
     * @return List of matching buses
     */
    List<Bus> searchBuses(String source, String destination, String date);
    
    /**
     * Save or update a bus
     * @param bus Bus entity to save
     * @return Saved bus entity
     */
    Bus saveBus(Bus bus);
    
    /**
     * Delete a bus by ID
     * @param id Bus ID to delete
     */
    void deleteBus(Long id);
    
    /**
     * Get bus by ID
     * @param id Bus ID
     * @return Bus entity or null if not found
     */
    Bus getBusById(Long id);
    
    /**
     * Get all unique source locations
     * @return List of source locations
     */
    List<String> getAllSources();
    
    /**
     * Get all unique destination locations
     * @return List of destination locations
     */
    List<String> getAllDestinations();
}