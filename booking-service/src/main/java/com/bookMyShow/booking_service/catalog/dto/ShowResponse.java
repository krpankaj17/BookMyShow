
package com.bookMyShow.booking_service.catalog.dto;

import java.time.LocalDateTime;

public record ShowResponse(
        String id,
        MovieSummary movie,
        VenueSummary venue,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String language,
        Boolean active
) {
    public record MovieSummary(
            String id,
            String title,
            String genre,
            Integer durationMinutes
    ) {}

    public record VenueSummary(
            String id,
            String name,
            String address,
            String city
    ) {}
}
