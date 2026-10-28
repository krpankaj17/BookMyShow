package com.bookMyShow.booking_service.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VenueRequest(
        @NotBlank(message = "Movie title is required")
        @Size(max = 150, message = "Movie title cannot exceed 150 characters")
        String name,
        @NotBlank(message = "Venue address is required")
        @Size(max = 500, message = "Venue address cannot exceed 500 characters")
        String address,
        @NotBlank(message = "Venue city is required")
        @Size(max = 150, message = "Venue city cannot exceed 150 characters")
        String city
) {
}
