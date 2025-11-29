package com.busbooking.system.service;

import com.busbooking.system.model.Bus;
import com.busbooking.system.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of BusService interface
 * Handles business logic for bus operations including search and filtering
 */
@Service
public class BusServiceImpl implements BusService {

    @Autowired
    private BusRepository busRepository;

    @Override
    public List<Bus> getAllBuses() {
        System.out.println("🚌 Getting all buses from database...");
        List<Bus> buses = busRepository.findAll();
        System.out.println("✅ Found " + buses.size() + " buses");
        return buses;
    }

    @Override
    public List<Bus> getActiveBuses() {
        System.out.println("🚌 Getting active buses...");
        List<Bus> activeBuses = busRepository.findAll().stream()
                .filter(bus -> bus.isActive())
                .collect(Collectors.toList());
        System.out.println("✅ Found " + activeBuses.size() + " active buses");
        return activeBuses;
    }

    @Override
    public List<Bus> searchBuses(String source, String destination, String date) {
        System.out.println("🔍 Searching buses: " + source + " → " + destination + " on " + date);
        
        // SIMPLIFIED SEARCH - Just filter by source and destination for now
        List<Bus> allBuses = busRepository.findAll();
        
        List<Bus> filteredBuses = allBuses.stream()
                .filter(bus -> bus.isActive())
                .filter(bus -> source == null || source.isEmpty() || 
                              bus.getSource().equalsIgnoreCase(source))
                .filter(bus -> destination == null || destination.isEmpty() || 
                              bus.getDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
        
        System.out.println("✅ Search found " + filteredBuses.size() + " buses");
        return filteredBuses;
    }

    @Override
    public Bus saveBus(Bus bus) {
        System.out.println("💾 Saving bus: " + bus.getBusName());
        return busRepository.save(bus);
    }

    @Override
    public void deleteBus(Long id) {
        System.out.println("🗑️ Deleting bus with ID: " + id);
        busRepository.deleteById(id);
    }

    @Override
    public Bus getBusById(Long id) {
        System.out.println("🔍 Getting bus by ID: " + id);
        Bus bus = busRepository.findById(id).orElse(null);
        if (bus == null) {
            System.out.println("❌ Bus not found with ID: " + id);
        } else {
            System.out.println("✅ Found bus: " + bus.getBusName());
        }
        return bus;
    }

    @Override
    public List<String> getAllSources() {
        List<String> sources = busRepository.findAll().stream()
                .map(Bus::getSource)
                .distinct()
                .collect(Collectors.toList());
        System.out.println("📍 Available sources: " + sources);
        return sources;
    }

    @Override
    public List<String> getAllDestinations() {
        List<String> destinations = busRepository.findAll().stream()
                .map(Bus::getDestination)
                .distinct()
                .collect(Collectors.toList());
        System.out.println("🎯 Available destinations: " + destinations);
        return destinations;
    }
}