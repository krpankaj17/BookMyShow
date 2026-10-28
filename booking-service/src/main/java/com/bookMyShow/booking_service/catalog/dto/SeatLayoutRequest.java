
package com.bookMyShow.booking_service.catalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SeatLayoutRequest(

        @NotBlank(message = "Venue ID cannot be blank")
        String venueId,

        @Min(value = 1, message = "At least one row is required")
        @Max(value = 26, message = "Maximum 26 rows are supported")
        int numberOfRows,

        @Min(value = 1, message = "At least one seat per row is required")
        @Max(value = 50, message = "Maximum 50 seats per row are supported")
        int seatsPerRow
) {
}
