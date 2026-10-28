package com.bookMyShow.api_gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("user-service")
                .route(RequestPredicates.path("/api/users/**").or(RequestPredicates.path("/api/auth/**")), http())
                .before(uri("http://localhost:8081"))
                .add(route("booking-service")
                        .route(RequestPredicates.path("/api/shows/**")
                                .or(RequestPredicates.path("/api/bookings/**"))
                                .or(RequestPredicates.path("/api/movies/**"))
                                .or(RequestPredicates.path("/api/venues/**"))
                                .or(RequestPredicates.path("/api/seats/**")), http())
                        .before(uri("http://localhost:8082"))
                        .build())
                .add(route("payment-service")
                        .route(RequestPredicates.path("/api/payments/**"), http())
                        .before(uri("http://localhost:8083"))
                        .build())
                .add(route("ticket-service")
                        .route(RequestPredicates.path("/api/tickets/**"), http())
                        .before(uri("http://localhost:8084"))
                        .build())
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
