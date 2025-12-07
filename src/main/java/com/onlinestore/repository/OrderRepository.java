package com.onlinestore.repository;

import com.onlinestore.model.Order;
import com.onlinestore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByUserId(Long userId);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}










