package com.bookMyShow.booking_service.catalog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "venues")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Venue {
    @Id
    private  String id;
    private String name;
    private String address;
    private String city ;
    @Builder.Default
    private  Boolean active = Boolean.TRUE;
}
