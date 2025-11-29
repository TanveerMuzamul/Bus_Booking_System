package com.busbooking.system.service;

import com.busbooking.system.model.UserRequest;
import com.busbooking.system.repository.UserRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserRequestServiceImpl implements UserRequestService {

    @Autowired
    private UserRequestRepository userRequestRepository;

    @Override
    public UserRequest createRequest(UserRequest request) {
        return userRequestRepository.save(request);
    }

    @Override
    public List<UserRequest> getRequestsByUser(String username) {
        return userRequestRepository.findByUsername(username);
    }

    @Override
    public List<UserRequest> getAllRequests() {
        return userRequestRepository.findAll();
    }

    @Override
    public List<UserRequest> getRequestsByStatus(String status) {
        return userRequestRepository.findByStatus(status);
    }

    @Override
    public UserRequest getRequestById(Long id) {
        return userRequestRepository.findById(id).orElse(null);
    }

    @Override
    public UserRequest updateRequestStatus(Long id, String status, String adminResponse) {
        UserRequest request = getRequestById(id);
        if (request != null) {
            request.setStatus(status);
            request.setAdminResponse(adminResponse);
            return userRequestRepository.save(request);
        }
        return null;
    }

    @Override
    public void deleteRequest(Long id) {
        userRequestRepository.deleteById(id);
    }

    @Override
    public Long getPendingRequestsCount() {
        return userRequestRepository.countPendingRequests();
    }
}