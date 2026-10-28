package com.bookMyShow.booking_service.catalog.dto;

public record MovieResponse( String id,
         String title,
         String description,
         String genre,
         String language,
         Integer durationMinutes,
         Boolean active) {
}
