package com.busbooking.system.repository;

import com.busbooking.system.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUsername(String username);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.username = :username")
    void deleteByUsername(String username);
}