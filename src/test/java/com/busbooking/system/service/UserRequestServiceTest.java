package com.busbooking.system.service;

import com.busbooking.system.model.UserRequest;
import com.busbooking.system.repository.UserRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserRequestService
 */
@ExtendWith(MockitoExtension.class)
class UserRequestServiceTest {

    @Mock
    private UserRequestRepository userRequestRepository;

    @InjectMocks
    private UserRequestServiceImpl userRequestService;

    private UserRequest testRequest1;
    private UserRequest testRequest2;

    @BeforeEach
    void setUp() {
        testRequest1 = new UserRequest("user1", "ACCOUNT_UPDATE", "Please update my email");
        testRequest1.setId(1L);
        testRequest1.setStatus("PENDING");

        testRequest2 = new UserRequest("user2", "REFUND_REQUEST", "Requesting refund for booking #123");
        testRequest2.setId(2L);
        testRequest2.setStatus("APPROVED");
    }

    @Test
    void testCreateRequest() {
        // Arrange
        when(userRequestRepository.save(any(UserRequest.class))).thenReturn(testRequest1);

        // Act
        UserRequest savedRequest = userRequestService.createRequest(testRequest1);

        // Assert
        assertNotNull(savedRequest);
        assertEquals("ACCOUNT_UPDATE", savedRequest.getRequestType());
        verify(userRequestRepository, times(1)).save(testRequest1);
    }

    @Test
    void testGetRequestsByUser() {
        // Arrange
        when(userRequestRepository.findByUsername("user1")).thenReturn(Arrays.asList(testRequest1));

        // Act
        List<UserRequest> requests = userRequestService.getRequestsByUser("user1");

        // Assert
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals("user1", requests.get(0).getUsername());
        verify(userRequestRepository, times(1)).findByUsername("user1");
    }

    @Test
    void testGetAllRequests() {
        // Arrange
        when(userRequestRepository.findAll()).thenReturn(Arrays.asList(testRequest1, testRequest2));

        // Act
        List<UserRequest> requests = userRequestService.getAllRequests();

        // Assert
        assertNotNull(requests);
        assertEquals(2, requests.size());
        verify(userRequestRepository, times(1)).findAll();
    }

    @Test
    void testGetRequestsByStatus() {
        // Arrange
        when(userRequestRepository.findByStatus("PENDING")).thenReturn(Arrays.asList(testRequest1));

        // Act
        List<UserRequest> requests = userRequestService.getRequestsByStatus("PENDING");

        // Assert
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals("PENDING", requests.get(0).getStatus());
        verify(userRequestRepository, times(1)).findByStatus("PENDING");
    }

    @Test
    void testGetRequestById() {
        // Arrange
        when(userRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest1));

        // Act
        UserRequest foundRequest = userRequestService.getRequestById(1L);

        // Assert
        assertNotNull(foundRequest);
        assertEquals(1L, foundRequest.getId());
        verify(userRequestRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateRequestStatus() {
        // Arrange
        when(userRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest1));
        when(userRequestRepository.save(any(UserRequest.class))).thenReturn(testRequest1);

        // Act
        UserRequest updatedRequest = userRequestService.updateRequestStatus(1L, "APPROVED", "Request approved");

        // Assert
        assertNotNull(updatedRequest);
        assertEquals("APPROVED", updatedRequest.getStatus());
        assertEquals("Request approved", updatedRequest.getAdminResponse());
        verify(userRequestRepository, times(1)).findById(1L);
        verify(userRequestRepository, times(1)).save(testRequest1);
    }

    @Test
    void testDeleteRequest() {
        // Arrange
        doNothing().when(userRequestRepository).deleteById(1L);

        // Act
        userRequestService.deleteRequest(1L);

        // Assert
        verify(userRequestRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetPendingRequestsCount() {
        // Arrange
        when(userRequestRepository.countPendingRequests()).thenReturn(5L);

        // Act
        long count = userRequestService.getPendingRequestsCount();

        // Assert
        assertEquals(5L, count);
        verify(userRequestRepository, times(1)).countPendingRequests();
    }
}