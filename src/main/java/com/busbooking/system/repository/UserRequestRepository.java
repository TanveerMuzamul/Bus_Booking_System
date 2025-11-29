package com.busbooking.system.repository;

import com.busbooking.system.model.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {
    List<UserRequest> findByUsername(String username);
    List<UserRequest> findByStatus(String status);
    
    @Query("SELECT COUNT(u) FROM UserRequest u WHERE u.status = 'PENDING'")
    Long countPendingRequests();
}