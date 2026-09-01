package com.example.ordersystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ordersystem.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * カテゴリで絞り込む。
     * category が null の場合は IS NULL 条件が成立し、全件を返す（フェーズD）。
     */
    @Query("SELECT p FROM Product p WHERE (:category IS NULL OR p.category = :category)")
    List<Product> findByCategory(@Param("category") String category);
}
