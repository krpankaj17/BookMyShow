package com.bookMyShow.user_service.dto;

import com.bookMyShow.user_service.enums.Role;

public record UserResponse(
        String id,
        String name,
        String email,
        String phoneNumber,
        Role role,
        java.time.LocalDateTime createdAt
) {
}