package com.busbooking.system.service;

import com.busbooking.system.model.Cart;
import com.busbooking.system.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public Cart addToCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getCartByUser(String username) {
        return cartRepository.findByUsername(username);
    }

    @Override
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public void clearCart(String username) {
        cartRepository.deleteByUsername(username);
    }

    @Override
    public double getCartTotal(String username) {
        return cartRepository.findByUsername(username).stream()
                .mapToDouble(Cart::getTotalPrice)
                .sum();
    }
}