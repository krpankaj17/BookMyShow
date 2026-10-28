package com.bookMyShow.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank(message = "Booking ID is required")
        String bookingId,

        @NotBlank(message = "User ID is required")
        String userId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Payment method is required")
        String paymentMethod
) {}
