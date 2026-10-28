package com.bookMyShow.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 150,message = "Name cannot exceed 150 characters")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        String password,
        @NotBlank(message = "Phone number is required")
        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        String phoneNumber
) {
}
