package com.bookMyShow.booking_service.catalog.dto;
import jakarta.validation.constraints.Size;

public record VenueUpdateRequest (

        @Size(max = 150, message = "Venue name cannot exceed 150 characters")
        String name,

        @Size(max = 500, message = "Venue address cannot exceed 500 characters")
        String address,

        @Size(max = 150, message = "Venue city cannot exceed 150 characters")
        String city,
        Boolean active
){
}
