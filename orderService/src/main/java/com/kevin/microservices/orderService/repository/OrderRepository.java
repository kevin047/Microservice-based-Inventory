package com.kevin.microservices.orderService.repository;

import com.kevin.microservices.orderService.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
