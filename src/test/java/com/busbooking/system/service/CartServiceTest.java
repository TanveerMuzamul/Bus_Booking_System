package com.busbooking.system.service;

import com.busbooking.system.model.Cart;
import com.busbooking.system.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CartService
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart testCart1;
    private Cart testCart2;

    @BeforeEach
    void setUp() {
        testCart1 = new Cart("user1", 1L, "CityLink Express", "Dublin", "Galway", 
                           "2024-01-01", 2, 51.00);
        testCart1.setId(1L);

        testCart2 = new Cart("user1", 2L, "CityLink Premium", "Dublin", "Cork", 
                           "2024-01-02", 1, 29.99);
        testCart2.setId(2L);
    }

    @Test
    void testAddToCart() {
        // Arrange
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart1);

        // Act
        Cart savedCart = cartService.addToCart(testCart1);

        // Assert
        assertNotNull(savedCart);
        assertEquals("CityLink Express", savedCart.getBusName());
        verify(cartRepository, times(1)).save(testCart1);
    }

    @Test
    void testGetCartByUser() {
        // Arrange
        when(cartRepository.findByUsername("user1")).thenReturn(Arrays.asList(testCart1, testCart2));

        // Act
        List<Cart> cartItems = cartService.getCartByUser("user1");

        // Assert
        assertNotNull(cartItems);
        assertEquals(2, cartItems.size());
        assertEquals("user1", cartItems.get(0).getUsername());
        verify(cartRepository, times(1)).findByUsername("user1");
    }

    @Test
    void testRemoveFromCart() {
        // Arrange
        doNothing().when(cartRepository).deleteById(1L);

        // Act
        cartService.removeFromCart(1L);

        // Assert
        verify(cartRepository, times(1)).deleteById(1L);
    }

    @Test
    void testClearCart() {
        // Arrange
        doNothing().when(cartRepository).deleteByUsername("user1");

        // Act
        cartService.clearCart("user1");

        // Assert
        verify(cartRepository, times(1)).deleteByUsername("user1");
    }

    @Test
    void testGetCartTotal() {
        // Arrange
        when(cartRepository.findByUsername("user1")).thenReturn(Arrays.asList(testCart1, testCart2));

        // Act
        double total = cartService.getCartTotal("user1");

        // Assert
        assertEquals(80.99, total, 0.01); // 51.00 + 29.99
        verify(cartRepository, times(1)).findByUsername("user1");
    }

    @Test
    void testGetCartTotalEmpty() {
        // Arrange
        when(cartRepository.findByUsername("emptyuser")).thenReturn(Arrays.asList());

        // Act
        double total = cartService.getCartTotal("emptyuser");

        // Assert
        assertEquals(0.0, total, 0.01);
        verify(cartRepository, times(1)).findByUsername("emptyuser");
    }
}