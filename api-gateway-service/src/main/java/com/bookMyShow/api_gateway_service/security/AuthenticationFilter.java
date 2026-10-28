package com.bookMyShow.api_gateway_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Order(1)
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isPublicPath(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.validateAndExtract(token);
        } catch (Exception ex) {
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
            return;
        }

        List<String> authorities = jwtUtil.extractAuthorities(claims);

        if (isOrganizerOrAdminPath(path, method)) {
            boolean hasPermission = authorities.contains("ROLE_ORGANIZER")
                    || authorities.contains("ROLE_ADMIN")
                    || authorities.contains("ORGANIZER")
                    || authorities.contains("ADMIN");

            if (!hasPermission) {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Access Denied: Requires ORGANIZER or ADMIN role");
                return;
            }
        }

        HeaderMapRequestWrapper wrappedRequest = new HeaderMapRequestWrapper(request);
        String userId = claims.get("userId", String.class);
        if (userId != null) {
            wrappedRequest.addHeader("X-User-Id", userId);
        }
        wrappedRequest.addHeader("X-User-Email", claims.getSubject());
        wrappedRequest.addHeader("X-User-Roles", String.join(",", authorities));

        filterChain.doFilter(wrappedRequest, response);
    }

    private boolean isPublicPath(String path, String method) {
        if (path.startsWith("/api/users/register")
                || path.startsWith("/api/users/login")
                || path.startsWith("/api/users/refresh")) {
            return true;
        }

        if ("GET".equalsIgnoreCase(method)) {
            return path.startsWith("/api/movies")
                    || path.startsWith("/api/venues")
                    || path.startsWith("/api/shows")
                    || path.startsWith("/api/bookings/shows/");
        }

        return false;
    }

    private boolean isOrganizerOrAdminPath(String path, String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return false;
        }

        return path.startsWith("/api/movies")
                || path.startsWith("/api/venues")
                || path.startsWith("/api/seats")
                || path.startsWith("/api/shows");
    }

    private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = "{\"status\":" + status + ",\"error\":\"" + message + "\"}";
        response.getWriter().write(json);
    }
}
