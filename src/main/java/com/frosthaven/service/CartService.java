package com.frosthaven.service;

import com.frosthaven.entity.Cart;
import com.frosthaven.entity.Product;
import com.frosthaven.repository.CartRepository;
import com.frosthaven.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository) {

        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<Cart> getUserCart(Integer userId) {

        return cartRepository.findByUserId(userId);
    }

    public Cart addToCart(
            Integer userId,
            Integer productId,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        Cart cart = cartRepository
                .findByUserIdAndProductId(userId, productId)
                .orElse(null);

        if (cart == null) {

            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);

        } else {

            int newQuantity =
                    cart.getQuantity() + quantity;

            if (newQuantity > product.getStock()) {
                throw new RuntimeException(
                        "Quantity exceeds available stock");
            }

            cart.setQuantity(newQuantity);
        }

        return cartRepository.save(cart);
    }

    public Cart updateQuantity(
            Integer cartId,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        Cart cart = cartRepository
                .findById(cartId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found"));

        Product product = productRepository
                .findById(cart.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        if (quantity > product.getStock()) {
            throw new RuntimeException(
                    "Quantity exceeds available stock");
        }

        cart.setQuantity(quantity);

        return cartRepository.save(cart);
    }

    public void removeFromCart(Integer cartId) {

        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("Cart item not found");
        }

        cartRepository.deleteById(cartId);
    }

    public void clearCart(Integer userId) {

        cartRepository.deleteByUserId(userId);
    }
}