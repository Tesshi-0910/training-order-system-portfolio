package com.example.ordersystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ordersystem.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    /** ユーザー名でユーザーを検索する（E-2 の CustomUserDetailsService から使用） */
    Optional<User> findByUsername(String username);

    /** ユーザー名の重複チェック（E-3 のアカウント登録処理から使用） */
    boolean existsByUsername(String username);
}
