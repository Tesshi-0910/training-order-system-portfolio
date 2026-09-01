package com.example.ordersystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ordersystem.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
