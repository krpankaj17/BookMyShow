package com.bookMyShow.user_service.entity;

import com.bookMyShow.user_service.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection =  "users")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {
    @Id
    private String id;
    @NotBlank(message = "Name is required")
    @Size(max = 150,message = "Name cannot exceed 150 characters")
    private String name;
    @Indexed(unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be least 8 characters")
    private String password;
    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;
    @NotNull(message = "Role is required")
    private Role role;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Builder.Default
    private boolean isActive = true;
}
