package com.example.ordersystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.ordersystem.entity.User;
import com.example.ordersystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    /** UT-E-01 */
    @Test
    void 正常系_ユーザー名でUserDetailsを返す() {
        User user = new User();
        user.setUsername("user1");
        user.setPassword("encodedPassword");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("user1");

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
    }

    /** UT-E-02 */
    @Test
    void 異常系_存在しないユーザー名で例外スロー() {
        when(userRepository.findByUsername("notexist")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("notexist"));
    }
}
