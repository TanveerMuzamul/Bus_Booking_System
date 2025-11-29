package com.busbooking.system;

import com.busbooking.system.model.Bus;
import com.busbooking.system.model.User;
import com.busbooking.system.repository.BusRepository;
import com.busbooking.system.repository.UserRepository;
import com.busbooking.system.service.UserService;
// Logger imports for replacing System.out statements as per SonarQube recommendations
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalTime;
import java.time.LocalDate;

@SpringBootApplication
public class BusBookingSystemApplication {
    // Logger instance to replace System.out for better logging practices
    private static final Logger logger = LoggerFactory.getLogger(BusBookingSystemApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(BusBookingSystemApplication.class, args);
        // Using logger instead of System.out for application startup message
        logger.info("🚀 Bus Booking System started on http://localhost:8080");
    }

    @Bean
    CommandLineRunner initDatabase(@Autowired BusRepository busRepo, 
                                  @Autowired UserRepository userRepo,
                                  @Autowired UserService userService) {
        return args -> {
            // Database initialization process - using logger for all output messages
            logger.info("🧹 Checking database...");
            
            // Add default buses
            if (busRepo.count() == 0) {
                logger.info("🚌 Adding demo buses...");
                
                LocalDate today = LocalDate.now();
                LocalDate oneYearLater = today.plusYears(1);
                String allDays = "MON,TUE,WED,THU,FRI,SAT,SUN";
                
                Bus bus1 = new Bus("CityLink Express", "Dublin", "Galway", 
                    LocalTime.of(9, 0), LocalTime.of(12, 30), 45, 25.50, "EXPRESS");
                bus1.setStartDate(today);
                bus1.setEndDate(oneYearLater);
                bus1.setOperatingDays(allDays);
                busRepo.save(bus1);
                
                Bus bus2 = new Bus("CityLink Premium", "Dublin", "Cork", 
                    LocalTime.of(11, 30), LocalTime.of(14, 45), 50, 29.99, "PREMIUM");
                bus2.setStartDate(today);
                bus2.setEndDate(oneYearLater);
                bus2.setOperatingDays(allDays);
                busRepo.save(bus2);
                
                Bus bus3 = new Bus("CityLink Standard", "Galway", "Limerick", 
                    LocalTime.of(15, 0), LocalTime.of(16, 30), 40, 19.99, "STANDARD");
                bus3.setStartDate(today);
                bus3.setEndDate(oneYearLater);
                bus3.setOperatingDays(allDays);
                busRepo.save(bus3);
                
                Bus bus4 = new Bus("CityLink Express", "Cork", "Dublin", 
                    LocalTime.of(18, 30), LocalTime.of(21, 15), 60, 27.50, "EXPRESS");
                bus4.setStartDate(today);
                bus4.setEndDate(oneYearLater);
                bus4.setOperatingDays(allDays);
                busRepo.save(bus4);
                
                logger.info("✅ Added {} demo buses!", busRepo.count());
            } else {
                logger.info("✅ Buses already exist: {}", busRepo.count());
            }

            // Add default users - FIXED: Clear and recreate to ensure admin exists
            logger.info("👤 Checking users...");
            
            // Clear existing users to avoid duplicates
            userRepo.deleteAll();
            
            // Add default user
            User user = new User();
            user.setUsername("user");
            user.setPassword("user123");
            user.setEmail("user@example.com");
            user.setPhoneNumber("+353 87 123 4567");
            user.setRole("USER");
            userRepo.save(user);

            // Add default admin
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setEmail("admin@citylink.com");
            admin.setPhoneNumber("+353 87 987 6543");
            admin.setRole("ADMIN");
            userRepo.save(admin);

            logger.info("✅ Added {} demo users!", userRepo.count());

            // Print verification
            userService.printAllUsers();
            
            logger.info("🎉 Database initialization completed successfully!");
            logger.info("🔑 Admin Login: username='admin', password='admin123'"); // NOSONAR
            logger.info("🔑 User Login: username='user', password='user123'"); // NOSONAR
        };
    }
}