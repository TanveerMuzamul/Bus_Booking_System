package com.busbooking.system.service;

import com.busbooking.system.model.User;
import com.busbooking.system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String VALIDATION_COMPLETE = "User validation completed";

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean validateUser(String username, String password) {
        logger.info("Validating user credentials");

        try {
            Optional<User> userOpt = userRepository.findFirstByUsername(username);

            if (userOpt.isPresent()) {
                boolean passwordMatch = password.equals(userOpt.get().getPassword());
                logger.info(VALIDATION_COMPLETE);
                return passwordMatch;
            }

            List<User> list = userRepository.findByUsername(username);
            if (!list.isEmpty()) {
                boolean passwordMatch = password.equals(list.get(0).getPassword());
                logger.info(VALIDATION_COMPLETE);
                return passwordMatch;
            }

            return false;

        } catch (RuntimeException e) {
            logger.error("Database error during validation: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void saveUser(User user) {
        try {
            logger.info("Saving user");

            if (user.getId() != null) {
                userRepository.save(user);
                return;
            }

            List<User> existing = userRepository.findByUsername(user.getUsername());
            if (!existing.isEmpty()) {
                throw new IllegalStateException("Username already exists");
            }

            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }

            userRepository.save(user);

        } catch (IllegalStateException e) {
            logger.error("User validation error: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected error saving user: {}", e.getMessage());
            throw new RuntimeException("Unexpected error saving user", e);
        }
    }

    @Override
    public void register(User user) {
        saveUser(user);
    }

    @Override
    public User login(String username, String password) {
        return userRepository.findFirstByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    @Override
    public List<User> getAllUsers() { return userRepository.findAll(); }

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
        return userRepository.findFirstByUsername(username).orElse(null);
    }

    @Override
    public void printAllUsers() {
        logger.info("===== USER LIST =====");
        userRepository.findAll().forEach(user ->
                logger.info("ID: {}, Username: {}, Email: {}", user.getId(), user.getUsername(), user.getEmail())
        );
        logger.info("=====================");
    }
}
