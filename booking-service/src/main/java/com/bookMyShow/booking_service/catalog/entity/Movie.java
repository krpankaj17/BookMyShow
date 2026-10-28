package com.bookMyShow.booking_service.catalog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "movies")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {
    @Id
    private String id ;
    private String title;
    private  String description;
    private String genre;
    private String language;
    private Integer durationMinutes;
    @Builder.Default
    private  Boolean active = Boolean.TRUE;
}
