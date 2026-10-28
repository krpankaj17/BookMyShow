
package com.bookMyShow.user_service.service;

import com.bookMyShow.user_service.dto.LoginRequest;
import com.bookMyShow.user_service.dto.LoginResponse;
import com.bookMyShow.user_service.dto.RefreshTokenRequest;
import com.bookMyShow.user_service.dto.RegisterRequest;
import com.bookMyShow.user_service.dto.UserResponse;
import com.bookMyShow.user_service.entity.User;
import com.bookMyShow.user_service.enums.Role;
import com.bookMyShow.user_service.repository.UserRepository;
import com.bookMyShow.user_service.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT)
        ).isPresent();
    }

    public UserResponse registerUser(RegisterRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (emailExists(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber().trim())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        com.bookMyShow.user_service.security.CustomUserDetails userDetails =
                (com.bookMyShow.user_service.security.CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String accessToken =
                jwtService.generateAccessToken(user.getId(), email, authorities);

        String refreshToken =
                jwtService.generateRefreshToken(user.getId(), email);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String token = request.refreshToken();

        io.jsonwebtoken.Claims claims;
        try {
            claims = jwtService.extractClaims(token);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        if (!"REFRESH".equals(claims.get("type", String.class))) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String email = claims.getSubject();
        String userId = claims.get("userId", String.class);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!user.isActive()) {
            throw new BadCredentialsException("User account is disabled");
        }

        List<String> authorities = List.of("ROLE_" + user.getRole().name());

        String newAccessToken = jwtService.generateAccessToken(userId, email, authorities);
        String newRefreshToken = jwtService.generateRefreshToken(userId, email);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
