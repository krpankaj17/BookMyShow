package com.bookMyShow.booking_service.catalog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "shows")
@CompoundIndex(
        name = "venue_active_time_idx",
        def = "{'venueId': 1, 'active': 1, 'startTime': 1, 'endTime': 1}"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Show {
    @Id
    private String id;

    @Indexed
    private String movieId;

    @Indexed
    private String venueId;

    @Indexed
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String language;

    @Builder.Default
    private Boolean active = Boolean.TRUE;
}
