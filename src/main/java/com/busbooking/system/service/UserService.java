package com.busbooking.system.service;

import com.busbooking.system.model.User;
import java.util.List;

/**
 * Service interface for user management operations
 * Handles user authentication, registration, and user management
 */
public interface UserService {
    boolean validateUser(String username, String password);
    void saveUser(User user);
    void register(User user);
    User login(String username, String password);
    
    // User management methods
    List<User> getAllUsers();
    User getUserById(Long id);
    void deleteUser(Long id);
    User findByUsername(String username);
    
    // Debug method
    void printAllUsers();
}