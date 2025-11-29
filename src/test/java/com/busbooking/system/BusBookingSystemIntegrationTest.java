package com.busbooking.system;

import com.busbooking.system.model.Bus;
import com.busbooking.system.model.User;
import com.busbooking.system.repository.BusRepository;
import com.busbooking.system.repository.UserRepository;
import com.busbooking.system.service.BusService;
import com.busbooking.system.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Bus Booking System
 * Uses H2 in-memory database and random port to avoid conflicts
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties") // CHANGED TO TEST PROPERTIES
public class BusBookingSystemIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusService busService;

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        assertNotNull(busRepository);
        assertNotNull(userRepository);
        assertNotNull(busService);
        assertNotNull(userService);
        System.out.println("✅ Spring context loaded successfully on port: " + port);
    }

    @Test
    void testDatabaseInitialization() {
        System.out.println("🧹 Checking database...");
        
        // Test bus initialization
        long busCount = busRepository.count();
        assertTrue(busCount >= 0, "Bus repository should be accessible");
        System.out.println("✅ Bus repository accessible. Count: " + busCount);

        // Test user initialization  
        long userCount = userRepository.count();
        assertTrue(userCount >= 0, "User repository should be accessible");
        System.out.println("✅ User repository accessible. Count: " + userCount);
    }

    @Test
    void testBusServiceOperations() {
        System.out.println("🚌 Testing Bus Service Operations...");

        // Create a test bus
        Bus testBus = new Bus("Test Bus", "Dublin", "Galway", 
            LocalTime.of(10, 0), LocalTime.of(13, 0), 50, 20.0, "STANDARD");
        
        // Save bus
        Bus savedBus = busService.saveBus(testBus);
        assertNotNull(savedBus.getId());
        System.out.println("💾 Saved bus: " + savedBus.getBusName());

        // Retrieve bus
        Bus retrievedBus = busService.getBusById(savedBus.getId());
        assertNotNull(retrievedBus);
        assertEquals("Test Bus", retrievedBus.getBusName());
        System.out.println("🔍 Retrieved bus: " + retrievedBus.getBusName());

        // Get all buses
        List<Bus> allBuses = busService.getAllBuses();
        assertFalse(allBuses.isEmpty());
        System.out.println("✅ Found " + allBuses.size() + " buses");
    }

    @Test
    void testUserServiceOperations() {
        System.out.println("👤 Testing User Service Operations...");

        // Create a test user
        User testUser = new User();
        testUser.setUsername("integrationuser");
        testUser.setPassword("integrationpass");
        testUser.setEmail("integration@test.com");
        testUser.setRole("USER");

        // Save user
        userService.saveUser(testUser);
        System.out.println("💾 Saved user: " + testUser.getUsername());

        // Validate user
        boolean isValid = userService.validateUser("integrationuser", "integrationpass");
        assertTrue(isValid, "User validation should pass");
        System.out.println("✅ User validation successful");

        // Find user by username
        User foundUser = userService.findByUsername("integrationuser");
        assertNotNull(foundUser);
        assertEquals("integrationuser", foundUser.getUsername());
        System.out.println("👤 User details - Username: " + foundUser.getUsername() + ", Role: " + foundUser.getRole());
    }

    @Test
    void testBusSearchFunctionality() {
        System.out.println("🔍 Testing Bus Search Functionality...");

        // Get all buses to verify search base
        List<Bus> allBuses = busService.getAllBuses();
        System.out.println("🚌 Getting all buses from database...");
        System.out.println("✅ Found " + allBuses.size() + " buses");

        // Test getting active buses
        List<Bus> activeBuses = busService.getActiveBuses();
        assertNotNull(activeBuses);
        System.out.println("✅ Active buses: " + activeBuses.size());

        // Test getting sources and destinations
        List<String> sources = busService.getAllSources();
        List<String> destinations = busService.getAllDestinations();
        assertNotNull(sources);
        assertNotNull(destinations);
        System.out.println("📍 Available sources: " + sources);
        System.out.println("🎯 Available destinations: " + destinations);
    }
}