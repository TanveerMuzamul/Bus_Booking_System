package com.busbooking.system.service;

import com.busbooking.system.model.UserRequest;
import java.util.List;

public interface UserRequestService {
    UserRequest createRequest(UserRequest request);
    List<UserRequest> getRequestsByUser(String username);
    List<UserRequest> getAllRequests();
    List<UserRequest> getRequestsByStatus(String status);
    UserRequest getRequestById(Long id);
    UserRequest updateRequestStatus(Long id, String status, String adminResponse);
    void deleteRequest(Long id);
    Long getPendingRequestsCount();
}