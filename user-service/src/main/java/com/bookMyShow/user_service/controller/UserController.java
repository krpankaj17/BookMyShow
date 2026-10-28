package com.bookMyShow.user_service.controller;

import com.bookMyShow.user_service.dto.LoginRequest;
import com.bookMyShow.user_service.dto.LoginResponse;
import com.bookMyShow.user_service.dto.RefreshTokenRequest;
import com.bookMyShow.user_service.dto.RegisterRequest;
import com.bookMyShow.user_service.dto.UserResponse;
import com.bookMyShow.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/api/users/register")
    public UserResponse registerUser(@Valid @RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }
    @PostMapping("/api/users/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/api/users/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return userService.refreshToken(request);
    }
}
