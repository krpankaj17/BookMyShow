package com.bookMyShow.ticket_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record TicketIssueRequest(
        @NotBlank(message = "Booking ID is required")
        String bookingId,

        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Show ID is required")
        String showId,

        @NotEmpty(message = "Seat numbers cannot be empty")
        List<String> seatNumbers,

        @NotNull(message = "Total amount is required")
        @Positive(message = "Total amount must be positive")
        BigDecimal totalAmount
) {}
