
package com.bookMyShow.booking_service.catalog.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ShowUpdateRequest(
        @Size(min = 1, message = "Movie ID cannot be empty")
        String movieId,

        @Size(min = 1, message = "Venue ID cannot be empty")
        String venueId,

        @Future(message = "Show start time must be in the future")
        LocalDateTime startTime,

        @Future(message = "Show end time must be in the future")
        LocalDateTime endTime,

        @Size(min = 1, max = 50, message = "Language cannot exceed 50 characters")
        String language,

        Boolean active
) {}
