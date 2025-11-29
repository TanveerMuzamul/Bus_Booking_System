package com.busbooking.system.service;

import com.busbooking.system.model.User;
import com.busbooking.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean validateUser(String username, String password) {
        System.out.println("🔍 Validating user: " + username);
        
        try {
            // Method 1: Try findFirstByUsername
            Optional<User> userOptional = userRepository.findFirstByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                boolean passwordMatch = user.getPassword().equals(password);
                System.out.println("✅ User found via findFirstByUsername - Password match: " + passwordMatch);
                System.out.println("👤 User details - Username: " + user.getUsername() + ", Role: " + user.getRole());
                return passwordMatch;
            }
            
            // Method 2: Try findByUsername (returns list)
            List<User> users = userRepository.findByUsername(username);
            if (!users.isEmpty()) {
                User user = users.get(0);
                boolean passwordMatch = user.getPassword().equals(password);
                System.out.println("✅ User found via findByUsername - Password match: " + passwordMatch);
                System.out.println("👤 User details - Username: " + user.getUsername() + ", Role: " + user.getRole());
                return passwordMatch;
            }
            
            // Method 3: Try direct database query as fallback
            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                if (user.getUsername().equals(username)) {
                    boolean passwordMatch = user.getPassword().equals(password);
                    System.out.println("✅ User found via findAll - Password match: " + passwordMatch);
                    System.out.println("👤 User details - Username: " + user.getUsername() + ", Role: " + user.getRole());
                    return passwordMatch;
                }
            }
            
            System.out.println("❌ User not found: " + username);
            return false;
            
        } catch (Exception e) {
            System.out.println("❌ Error validating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ... rest of your existing UserServiceImpl methods remain the same
    @Override
    public void saveUser(User user) {
        try {
            System.out.println("💾 Saving user: " + user.getUsername());
            
            // For existing users, allow saving without duplicate check
            if (user.getId() != null) {
                userRepository.save(user);
                System.out.println("✅ Existing user updated");
                return;
            }
            
            // For new users, check if username already exists
            List<User> existingUsers = userRepository.findByUsername(user.getUsername());
            if (!existingUsers.isEmpty()) {
                throw new RuntimeException("Username '" + user.getUsername() + "' already exists!");
            }
            
            // Default role for new users
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }
            
            userRepository.save(user);
            System.out.println("✅ New user saved successfully");
            
        } catch (Exception e) {
            System.out.println("❌ Error saving user: " + e.getMessage());
            throw e;
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
        System.out.println("====== ALL USERS IN DATABASE ======");
        for (User user : users) {
            System.out.println("ID: " + user.getId() + 
                             ", Username: " + user.getUsername() + 
                             ", Password: " + user.getPassword() + 
                             ", Email: " + user.getEmail() +
                             ", Phone: " + user.getPhoneNumber() +
                             ", Role: " + user.getRole());
        }
        System.out.println("===================================");
    }
}