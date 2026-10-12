
package com.frosthaven.controller;

import com.frosthaven.entity.Order;
import com.frosthaven.entity.OrderItem;
import com.frosthaven.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody CheckoutRequest request) {

        try {
            if (request.getCustomerName() == null
                    || request.getCustomerName().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Customer name is required."));
            }

            if (request.getPhone() == null
                    || request.getPhone().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Phone number is required."));
            }

            if (request.getDeliveryAddress() == null
                    || request.getDeliveryAddress().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Delivery address is required."));
            }

            if (request.getPaymentMethod() == null
                    || request.getPaymentMethod().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Payment method is required."));
            }

            Order order = new Order();
            order.setCustomerName(request.getCustomerName().trim());
            order.setPhone(request.getPhone().trim());
            order.setAddress(request.getDeliveryAddress().trim());
            order.setPaymentMethod(request.getPaymentMethod());

            Order savedOrder = orderService.createOrder(
                    order,
                    request.getItems()
            );

            return ResponseEntity.ok(savedOrder);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(
                orderService.getOrdersByUser(userId)
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(
            @PathVariable Integer orderId) {
        try {
            Order order = orderService.getOrder(orderId);
            List<OrderItem> items =
                    orderService.getOrderItems(orderId);

            return ResponseEntity.ok(
                    Map.of("order", order, "items", items)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");

            if (status == null || status.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status is required."));
            }

            return ResponseEntity.ok(
                    orderService.updateOrderStatus(orderId, status)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public static class CheckoutRequest {

        private String customerName;
        private String email;
        private String phone;
        private String deliveryAddress;
        private String paymentMethod;
        private List<OrderItem> items;

        public CheckoutRequest() {
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getDeliveryAddress() {
            return deliveryAddress;
        }

        public void setDeliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public List<OrderItem> getItems() {
            return items;
        }

        public void setItems(List<OrderItem> items) {
            this.items = items;
        }
    }
}
