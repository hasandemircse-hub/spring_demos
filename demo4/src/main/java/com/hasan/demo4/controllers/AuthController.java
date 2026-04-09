package com.hasan.demo4.controllers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hasan.demo4.entities.User;
import com.hasan.demo4.repositories.UserRepository;
import com.hasan.demo4.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@Tag(name = "Auth", description = "Kullanıcı kayıt ve giriş işlemleri")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "Kullanıcı kaydı")
    @SecurityRequirements
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "Kayıt başarılı";
    }

    @Operation(summary = "Giriş yap — JWT token döner")
    @SecurityRequirements
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        return jwtService.generateToken(user.getUsername());
    }
}
