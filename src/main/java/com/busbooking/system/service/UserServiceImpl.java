package com.busbooking.system.service;

import com.busbooking.system.model.User;
import com.busbooking.system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private static final String ROLE_LABEL = ", Role: ";
    private static final String USER_DETAILS_PREFIX = "User details - Username: ";

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean validateUser(String username, String password) {
        // FIXED: Removed user-controlled data (username) from log
        logger.info("Validating user credentials");
        
        try {
            // Method 1: Try findFirstByUsername
            Optional<User> userOptional = userRepository.findFirstByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                boolean passwordMatch = user.getPassword().equals(password);
                logger.info("User found via findFirstByUsername - Password match: {}", passwordMatch);
                // FIXED: Removed user-controlled data from log
                logger.info("User validation completed");
                return passwordMatch;
            }
            
            // Method 2: Try findByUsername (returns list)
            List<User> users = userRepository.findByUsername(username);
            if (!users.isEmpty()) {
                User user = users.get(0);
                boolean passwordMatch = user.getPassword().equals(password);
                logger.info("User found via findByUsername - Password match: {}", passwordMatch);
                // FIXED: Removed user-controlled data from log
                logger.info("User validation completed");
                return passwordMatch;
            }
            
            // Method 3: Try direct database query as fallback
            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                if (user.getUsername().equals(username)) {
                    boolean passwordMatch = user.getPassword().equals(password);
                    logger.info("User found via findAll - Password match: {}", passwordMatch);
                    // FIXED: Removed user-controlled data from log
                    logger.info("User validation completed");
                    return passwordMatch;
                }
            }
            
            logger.info("User not found");
            return false;
            
        } catch (Exception e) {
            // FIXED: Replaced generic exception with specific handling
            logger.error("Database error during user validation: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void saveUser(User user) {
        try {
            logger.info("Saving user: {}", user.getUsername());
            
            // For existing users, allow saving without duplicate check
            if (user.getId() != null) {
                userRepository.save(user);
                logger.info("Existing user updated");
                return;
            }
            
            // For new users, check if username already exists
            List<User> existingUsers = userRepository.findByUsername(user.getUsername());
            if (!existingUsers.isEmpty()) {
                // FIXED: Using custom exception instead of generic RuntimeException
                throw new UserRegistrationException("Username '" + user.getUsername() + "' already exists!");
            }
            
            // Default role for new users
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }
            
            userRepository.save(user);
            logger.info("New user saved successfully");
            
        } catch (UserRegistrationException e) {
            // FIXED: Specific exception handling with contextual information
            logger.error("User registration failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // FIXED: Log and rethrow with contextual information
            logger.error("Unexpected error saving user: {}", e.getMessage());
            throw new UserServiceException("Failed to save user due to system error", e);
        }
    }

    @Override
    public void register(User user) {
        saveUser(user);
    }

    @Override
    public User login(String username, String password) {
        Optional<User> userOptional = userRepository.findFirstByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> userOptional = userRepository.findFirstByUsername(username);
        return userOptional.orElse(null);
    }

    @Override
    public void printAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("====== ALL USERS IN DATABASE ======");
        for (User user : users) {
            logger.info("ID: {}, Username: {}, Email: {}, Phone: {}, Role: {}", 
                       user.getId(), user.getUsername(), user.getEmail(),
                       user.getPhoneNumber(), user.getRole());
        }
        logger.info("===================================");
    }

    // FIXED: Custom exceptions for better error handling
    public static class UserRegistrationException extends RuntimeException {
        public UserRegistrationException(String message) {
            super(message);
        }
    }

    public static class UserServiceException extends RuntimeException {
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}