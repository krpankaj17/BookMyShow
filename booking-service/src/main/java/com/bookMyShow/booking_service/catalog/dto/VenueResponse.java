package com.bookMyShow.booking_service.catalog.dto;

public record VenueResponse (
        String id,
        String name,
        String address,
        String city,
        Boolean active
){
}
