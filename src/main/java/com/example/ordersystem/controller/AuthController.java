package com.example.ordersystem.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ordersystem.entity.User;
import com.example.ordersystem.form.RegisterForm;
import com.example.ordersystem.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** ログイン画面表示 (GET /login) */
    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    /** アカウント登録フォーム表示 (GET /register) */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    /** アカウント登録実行 (POST /register)（UT-E-03, UT-E-04） */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (userRepository.existsByUsername(form.getUsername())) {
            model.addAttribute("usernameError", "このユーザー名は既に使用されています");
            return "auth/register";
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
