package com.frosthaven.controller;

import com.frosthaven.entity.Cart;
import com.frosthaven.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Cart>> getCart(
            @PathVariable Integer userId) {

        return ResponseEntity.ok(
                cartService.getUserCart(userId)
        );
    }

    @PostMapping
    public ResponseEntity<?> addToCart(
            @RequestBody CartRequest request) {

        try {

            Cart cart = cartService.addToCart(
                    request.getUserId(),
                    request.getProductId(),
                    request.getQuantity()
            );

            return ResponseEntity.ok(cart);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateCart(
            @PathVariable Integer cartId,
            @RequestBody Map<String, Integer> request) {

        try {

            Integer quantity = request.get("quantity");

            Cart cart = cartService.updateQuantity(
                    cartId,
                    quantity
            );

            return ResponseEntity.ok(cart);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Integer cartId) {

        try {

            cartService.removeFromCart(cartId);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Item removed from cart"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> clearCart(
            @PathVariable Integer userId) {

        cartService.clearCart(userId);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Cart cleared"
                )
        );
    }

    public static class CartRequest {

        private Integer userId;
        private Integer productId;
        private Integer quantity;

        public CartRequest() {
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}