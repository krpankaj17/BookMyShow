
package com.bookMyShow.booking_service.catalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MovieRequest(

        @NotBlank(message = "Movie title is required")
        @Size(max = 150, message = "Movie title cannot exceed 150 characters")
        String title,

        @NotBlank(message = "Movie description is required")
        @Size(max = 2000, message = "Movie description cannot exceed 2000 characters")
        String description,

        @NotBlank(message = "Movie genre is required")
        @Size(max = 50, message = "Genre cannot exceed 50 characters")
        String genre,

        @NotBlank(message = "Movie language is required")
        @Size(max = 50, message = "Language cannot exceed 50 characters")
        String language,

        @NotNull(message = "Movie duration is required")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        @Max(value = 600, message = "Duration cannot exceed 600 minutes")
        Integer durationMinutes
) {
}
