package com.frosthaven.repository;

import com.frosthaven.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserIdOrderByOrderDateDesc(Integer userId);

    List<Order> findAllByOrderByOrderDateDesc();
}