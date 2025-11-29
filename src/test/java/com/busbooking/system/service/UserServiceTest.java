package com.busbooking.system.service;

import com.busbooking.system.model.User;
import com.busbooking.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for UserService using H2 database
 * Can run while main application is running
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clear any existing test data
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("testpass");
        testUser.setEmail("test@example.com");
        testUser.setRole("USER");
        
        userService.saveUser(testUser);
    }

    @Test
    void testValidateUser_Success() {
        System.out.println("🔍 Validating user: testuser");
        boolean result = userService.validateUser("testuser", "testpass");
        assertTrue(result);
        System.out.println("✅ User found via findFirstByUsername - Password match: true");
    }

    @Test
    void testValidateUser_UserNotFound() {
        System.out.println("🔍 Validating user: nonexistent");
        boolean result = userService.validateUser("nonexistent", "password");
        assertFalse(result);
        System.out.println("❌ User not found: nonexistent");
    }

    @Test
    void testValidateUser_WrongPassword() {
        System.out.println("🔍 Validating user: testuser");
        boolean result = userService.validateUser("testuser", "wrongpassword");
        assertFalse(result);
        System.out.println("✅ User found via findFirstByUsername - Password match: false");
    }

    @Test
    void testSaveUser_NewUser() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpass");
        newUser.setEmail("new@example.com");
        
        System.out.println("💾 Saving user: " + newUser.getUsername());
        assertDoesNotThrow(() -> userService.saveUser(newUser));
        System.out.println("✅ New user saved successfully");
    }

    @Test
    void testSaveUser_ExistingUser() {
        // This user already exists from @BeforeEach
        testUser.setEmail("updated@example.com");
        
        System.out.println("💾 Saving user: " + testUser.getUsername());
        assertDoesNotThrow(() -> userService.saveUser(testUser));
        System.out.println("✅ Existing user updated");
    }

    @Test
    void testRegisterUser() {
        User newUser = new User();
        newUser.setUsername("registeruser");
        newUser.setPassword("registerpass");
        newUser.setEmail("register@example.com");
        
        System.out.println("💾 Saving user: " + newUser.getUsername());
        assertDoesNotThrow(() -> userService.register(newUser));
        System.out.println("✅ User registered successfully");
    }

    @Test
    void testLogin_Success() {
        System.out.println("🔍 Logging in user: testuser");
        User result = userService.login("testuser", "testpass");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        System.out.println("👤 User details - Username: " + result.getUsername() + ", Role: " + result.getRole());
    }

    @Test
    void testLogin_Failure() {
        System.out.println("🔍 Logging in user: testuser");
        User result = userService.login("testuser", "wrongpassword");
        assertNull(result);
        System.out.println("❌ Login failed - Wrong password");
    }

    @Test
    void testGetAllUsers() {
        System.out.println("📋 Getting all users...");
        List<User> result = userService.getAllUsers();
        assertFalse(result.isEmpty());
        System.out.println("✅ Found " + result.size() + " users");
    }

    @Test
    void testGetUserById_Found() {
        User savedUser = userService.findByUsername("testuser");
        System.out.println("🔍 Getting user by ID: " + savedUser.getId());
        
        User result = userService.getUserById(savedUser.getId());
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        System.out.println("✅ Found user: " + result.getUsername());
    }

    @Test
    void testGetUserById_NotFound() {
        System.out.println("🔍 Getting user by ID: 99");
        User result = userService.getUserById(99L);
        assertNull(result);
        System.out.println("❌ User not found with ID: 99");
    }

    @Test
    void testDeleteUser() {
        User savedUser = userService.findByUsername("testuser");
        System.out.println("🗑️ Deleting user with ID: " + savedUser.getId());
        
        userService.deleteUser(savedUser.getId());
        
        User deletedUser = userService.getUserById(savedUser.getId());
        assertNull(deletedUser);
        System.out.println("✅ User deleted successfully: " + savedUser.getId());
    }

    @Test
    void testFindByUsername_Found() {
        System.out.println("🔍 Finding user by username: testuser");
        User result = userService.findByUsername("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        System.out.println("✅ Found user: " + result.getUsername());
    }

    @Test
    void testFindByUsername_NotFound() {
        System.out.println("🔍 Finding user by username: unknown");
        User result = userService.findByUsername("unknown");
        assertNull(result);
        System.out.println("❌ User not found: unknown");
    }
}