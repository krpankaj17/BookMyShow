
package com.bookMyShow.booking_service.catalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovieUpdateRequest(

        @Size(min = 1, max = 150, message = "Movie title must be between 1 and 150 characters")
        String title,

        @Size(min = 1, max = 2000, message = "Movie description must be between 1 and 2000 characters")
        String description,

        @Size(min = 1, max = 50, message = "Genre cannot exceed 50 characters")
        String genre,

        @Size(min = 1, max = 50, message = "Language cannot exceed 50 characters")
        String language,

        @Min(value = 1, message = "Duration must be at least 1 minute")
        @Max(value = 600, message = "Duration cannot exceed 600 minutes")
        Integer durationMinutes,

        Boolean active
) {
}
