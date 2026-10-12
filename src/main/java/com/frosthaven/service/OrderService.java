
package com.frosthaven.service;

import com.frosthaven.entity.Order;
import com.frosthaven.entity.OrderItem;
import com.frosthaven.entity.Product;
import com.frosthaven.repository.OrderItemRepository;
import com.frosthaven.repository.OrderRepository;
import com.frosthaven.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Order order, List<OrderItem> items) {

        if (order == null) {
            throw new RuntimeException("Order details are missing.");
        }

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Your cart is empty.");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {

            if (item.getProductId() == null) {
                throw new RuntimeException("Product ID is missing.");
            }

            if (item.getQuantity() == null ||
                    item.getQuantity() <= 0) {
                throw new RuntimeException("Invalid quantity.");
            }

            Product product = productRepository
                    .findById(item.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found."));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for "
                                + product.getProductName());
            }

            BigDecimal price =
                    BigDecimal.valueOf(product.getPrice());

            BigDecimal itemTotal = price.multiply(
                    BigDecimal.valueOf(item.getQuantity()));

            total = total.add(itemTotal);
            item.setPrice(price);
        }

        order.setTotalAmount(total);

        if (order.getOrderStatus() == null ||
                order.getOrderStatus().isBlank()) {
            order.setOrderStatus("Pending");
        }

        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : items) {
            item.setOrderId(savedOrder.getId());
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public List<Order> getOrdersByUser(Integer userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found."));
    }

    public Order updateOrderStatus(Integer orderId, String status) {
        Order order = getOrder(orderId);
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }
}
