package com.hasan.demo4.services;

import com.hasan.demo4.dto.UserResponseDto;
import com.hasan.demo4.entities.User;
import com.hasan.demo4.exception.ResourceNotFoundException;
import com.hasan.demo4.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponseDto(u.getId(), u.getUsername(), u.getRole()))
                .toList();
    }

    public UserResponseDto create(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role != null ? role : "USER");
        user = userRepository.save(user);
        return new UserResponseDto(user.getId(), user.getUsername(), user.getRole());
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kullanıcı bulunamadı: " + id);
        }
        userRepository.deleteById(id);
    }
}
