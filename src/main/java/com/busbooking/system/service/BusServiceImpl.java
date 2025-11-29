package com.busbooking.system.service;

import com.busbooking.system.model.Bus;
import com.busbooking.system.repository.BusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementation of BusService interface
 * Handles business logic for bus operations including search and filtering
 */
@Service
public class BusServiceImpl implements BusService {

    private static final Logger logger = LoggerFactory.getLogger(BusServiceImpl.class);

    @Autowired
    private BusRepository busRepository;

    @Override
    public List<Bus> getAllBuses() {
        logger.info("Getting all buses from database");
        List<Bus> buses = busRepository.findAll();
        logger.info("Found {} buses", buses.size());
        return buses;
    }

    @Override
    public List<Bus> getActiveBuses() {
        logger.info("Getting active buses");
        List<Bus> activeBuses = busRepository.findAll().stream()
                .filter(Bus::isActive)
                .toList();
        logger.info("Found {} active buses", activeBuses.size());
        return activeBuses;
    }

    @Override
    public List<Bus> searchBuses(String source, String destination, String date) {
        // SECURITY FIX: Removed user-controlled data (source, destination, date) from log
        logger.info("Searching buses");
        
        // SIMPLIFIED SEARCH - Just filter by source and destination for now
        List<Bus> allBuses = busRepository.findAll();
        
        List<Bus> filteredBuses = allBuses.stream()
                .filter(Bus::isActive)
                .filter(bus -> source == null || source.isEmpty() || 
                              bus.getSource().equalsIgnoreCase(source))
                .filter(bus -> destination == null || destination.isEmpty() || 
                              bus.getDestination().equalsIgnoreCase(destination))
                .toList();
        
        logger.info("Search found {} buses", filteredBuses.size());
        return filteredBuses;
    }

    @Override
    public Bus saveBus(Bus bus) {
        logger.info("Saving bus: {}", bus.getBusName());
        return busRepository.save(bus);
    }

    @Override
    public void deleteBus(Long id) {
        logger.info("Deleting bus with ID: {}", id);
        busRepository.deleteById(id);
    }

    @Override
    public Bus getBusById(Long id) {
        logger.info("Getting bus by ID: {}", id);
        Bus bus = busRepository.findById(id).orElse(null);
        if (bus == null) {
            logger.error("Bus not found with ID: {}", id);
        } else {
            logger.info("Found bus: {}", bus.getBusName());
        }
        return bus;
    }

    @Override
    public List<String> getAllSources() {
        List<String> sources = busRepository.findAll().stream()
                .map(Bus::getSource)
                .distinct()
                .toList();
        // SECURITY FIX: Removed user-controlled data from log
        logger.info("Retrieved available sources");
        return sources;
    }

    @Override
    public List<String> getAllDestinations() {
        List<String> destinations = busRepository.findAll().stream()
                .map(Bus::getDestination)
                .distinct()
                .toList();
        // SECURITY FIX: Removed user-controlled data from log
        logger.info("Retrieved available destinations");
        return destinations;
    }
}