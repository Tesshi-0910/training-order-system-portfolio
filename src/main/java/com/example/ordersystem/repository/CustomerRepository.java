package com.example.ordersystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ordersystem.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * 顧客名で部分一致検索する。
     * name が null の場合は IS NULL 条件が成立し、全件を返す（フェーズD）。
     */
    @Query("SELECT c FROM Customer c WHERE (:name IS NULL OR c.customerName LIKE %:name%)")
    List<Customer> findByName(@Param("name") String name);
}
