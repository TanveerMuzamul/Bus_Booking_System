package com.busbooking.system.service;
/**
 * Service interface for shopping cart operations
 * Manages cart items, totals, and user cart sessions
 */
import com.busbooking.system.model.Cart;
import java.util.List;

public interface CartService {
    Cart addToCart(Cart cart);
    List<Cart> getCartByUser(String username);
    void removeFromCart(Long cartId);
    void clearCart(String username);
    double getCartTotal(String username);
}