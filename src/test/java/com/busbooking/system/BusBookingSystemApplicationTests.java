package com.busbooking.system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BusBookingSystemApplicationTests {

    @Test
    void contextLoads() {
        // Test that application context loads with H2 test database
        System.out.println("✅ Application context loaded successfully with H2 test database");
    }
}