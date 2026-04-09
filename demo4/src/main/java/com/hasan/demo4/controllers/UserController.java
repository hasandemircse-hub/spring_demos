package com.hasan.demo4.controllers;

import com.hasan.demo4.dto.UserResponseDto;
import com.hasan.demo4.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Users", description = "Kullanıcı yönetimi — sadece admin")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Tüm kullanıcıları listele")
    @GetMapping
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }

    @Operation(summary = "Yeni kullanıcı oluştur")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@RequestBody Map<String, String> body) {
        return userService.create(body.get("username"), body.get("password"), body.get("role"));
    }

    @Operation(summary = "Kullanıcı sil")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
