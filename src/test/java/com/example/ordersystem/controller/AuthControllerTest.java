package com.example.ordersystem.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.ordersystem.entity.User;
import com.example.ordersystem.form.RegisterForm;
import com.example.ordersystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    /** UT-E-03 */
    @Test
    void 正常系_新規ユーザー登録でリダイレクト() {
        RegisterForm form = new RegisterForm();
        form.setUsername("newuser");
        form.setPassword("pass");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        String view = authController.register(form, bindingResult, model);

        assertEquals("redirect:/login?registered", view);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /** UT-E-04 */
    @Test
    void 異常系_重複ユーザー名でエラー表示() {
        RegisterForm form = new RegisterForm();
        form.setUsername("existing");
        form.setPassword("pass");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        String view = authController.register(form, bindingResult, model);

        assertEquals("auth/register", view);
        verify(model, times(1)).addAttribute("usernameError", "このユーザー名は既に使用されています");
        verify(userRepository, times(0)).save(any(User.class));
    }
}
