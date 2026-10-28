
package com.bookMyShow.booking_service.catalog.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ShowRequest(

        @NotBlank(message = "Movie ID is required")
        String movieId,

        @NotBlank(message = "Venue ID is required")
        String venueId,

        @NotNull(message = "Show start time is required")
        @Future(message = "Show start time must be in the future")
        LocalDateTime startTime,

        @NotNull(message = "Show end time is required")
        @Future(message = "Show end time must be in the future")
        LocalDateTime endTime,

        @NotBlank(message = "Language is required")
        @Size(max = 50, message = "Language cannot exceed 50 characters")
        String language
) {}
