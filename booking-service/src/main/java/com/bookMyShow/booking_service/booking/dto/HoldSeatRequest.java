package com.bookMyShow.booking_service.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record HoldSeatRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Show ID cannot be blank")
        String showId,

        @NotEmpty(message = "At least one seat must be selected")
        List<String> seatNumbers
) {}
