package com.example.ordersystem.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ordersystem.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * ステータスで絞り込む。
     * status が null の場合は IS NULL 条件が成立し、全件を返す（フェーズD）。
     * JOIN FETCH o.customer で一覧表示に必要な顧客情報を同時取得し、N+1問題を回避する。
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE (:status IS NULL OR o.status = :status)")
    List<Order> findByStatus(@Param("status") String status);

    /**
     * ステータス・受注日範囲（開始日〜終了日）で絞り込む（フェーズF: UT-F-01〜UT-F-03）。
     * status/from/to はそれぞれ null の場合その条件は絞り込みに影響しない（3つとも null なら全件を返す）。
     * from のみ指定時は開始日以降、to のみ指定時は終了日以前のみで絞り込まれる。
     * JOIN FETCH o.customer で一覧表示に必要な顧客情報を同時取得し、N+1問題を回避する。
     *
     * from/to は「:from IS NULL OR ...」という書き方ではなく COALESCE(:from, o.orderDate) を用いている。
     * これは PostgreSQL + Hibernate の組み合わせで、日付型パラメータが「? IS NULL」のように
     * 型の手がかりが無い文脈だけで使われると "could not determine data type of parameter" という
     * エラーになる既知の問題を回避するため（結合テストIT-14で実際に発生した不具合の修正）。
     * COALESCE で o.orderDate 列と同じ date 型の文脈に置くことでパラメータの型が確定する。
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer "
            + "WHERE (:status IS NULL OR o.status = :status) "
            + "AND o.orderDate >= COALESCE(:from, o.orderDate) "
            + "AND o.orderDate <= COALESCE(:to, o.orderDate)")
    List<Order> findByCondition(@Param("status") String status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
