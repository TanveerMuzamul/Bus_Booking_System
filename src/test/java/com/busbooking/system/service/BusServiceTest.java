package com.busbooking.system.service;

import com.busbooking.system.model.Bus;
import com.busbooking.system.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class BusServiceTest {

    @Autowired
    private BusService busService;

    @Autowired
    private BusRepository busRepository;

    private Bus testBus;

    @BeforeEach
    void setUp() {
        // Clear any existing test data
        busRepository.deleteAll();

        // Create test bus
        testBus = new Bus("CityLink Express", "Dublin", "Galway", 
            LocalTime.of(9, 0), LocalTime.of(12, 30), 45, 25.50, "EXPRESS");
        testBus.setActive(true);
        
        busService.saveBus(testBus);
    }

    @Test
    void testGetBusById_NotFound() {
        System.out.println("🔍 Getting bus by ID: 99");
        Bus result = busService.getBusById(99L);
        assertNull(result);
        System.out.println("❌ Bus not found with ID: 99");
    }

    @Test
    void testSearchBuses() {
        System.out.println("🔍 Searching buses: Dublin → Galway on 2024-01-01");
        List<Bus> results = busService.searchBuses("Dublin", "Galway", "2024-01-01");
        assertNotNull(results);
        System.out.println("✅ Search found " + results.size() + " buses");
    }

    @Test
    void testGetAllBuses() {
        System.out.println("🚌 Getting all buses from database...");
        List<Bus> buses = busService.getAllBuses();
        assertFalse(buses.isEmpty());
        System.out.println("✅ Found " + buses.size() + " buses");
    }

    @Test
    void testSearchBuses_NoFilters() {
        System.out.println("🔍 Searching buses: null → null on null");
        List<Bus> results = busService.searchBuses(null, null, null);
        assertNotNull(results);
        System.out.println("✅ Search found " + results.size() + " buses");
    }

    @Test
    void testDeleteBus() {
        Bus savedBus = busService.saveBus(testBus);
        System.out.println("🗑️ Deleting bus with ID: " + savedBus.getId());
        
        busService.deleteBus(savedBus.getId());
        
        Bus deletedBus = busService.getBusById(savedBus.getId());
        assertNull(deletedBus);
        System.out.println("✅ Bus deleted successfully: " + savedBus.getId());
    }

    @Test
    void testGetBusById_Found() {
        Bus savedBus = busService.saveBus(testBus);
        System.out.println("🔍 Getting bus by ID: " + savedBus.getId());
        
        Bus result = busService.getBusById(savedBus.getId());
        assertNotNull(result);
        assertEquals("CityLink Express", result.getBusName());
        System.out.println("✅ Found bus: " + result.getBusName());
    }

    @Test
    void testGetAllSources() {
        System.out.println("📍 Available sources: " + busService.getAllSources());
        List<String> sources = busService.getAllSources();
        assertNotNull(sources);
        System.out.println("✅ Available sources: " + sources);
    }

    @Test
    void testGetAllDestinations() {
        System.out.println("🎯 Available destinations: " + busService.getAllDestinations());
        List<String> destinations = busService.getAllDestinations();
        assertNotNull(destinations);
        System.out.println("🎯 Available destinations: " + destinations);
    }

    @Test
    void testGetActiveBuses() {
        System.out.println("🚌 Getting active buses...");
        List<Bus> activeBuses = busService.getActiveBuses();
        assertNotNull(activeBuses);
        System.out.println("✅ Found " + activeBuses.size() + " active buses");
    }

    @Test
    void testSaveBus() {
        Bus newBus = new Bus("Test Save Bus", "Cork", "Limerick", 
            LocalTime.of(14, 0), LocalTime.of(16, 0), 30, 15.0, "STANDARD");
        
        System.out.println("💾 Saving bus: " + newBus.getBusName());
        Bus savedBus = busService.saveBus(newBus);
        
        assertNotNull(savedBus.getId());
        assertEquals("Test Save Bus", savedBus.getBusName());
        System.out.println("✅ Bus saved successfully: " + savedBus.getBusName());
    }
}