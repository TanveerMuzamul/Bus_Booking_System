package com.busbooking.system.repository;

import com.busbooking.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    List<User> findByUsername(String username);
    
    // Returns first user with matching username
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findFirstByUsername(@Param("username") String username);
    
    // Direct query for validation
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, 
                                           @Param("password") String password);
}