package com.bookMyShow.booking_service.catalog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "seats")
@CompoundIndex(
        name = "venue_seat_unique",
        def = "{'venueId': 1, 'seatNumber': 1}",
        unique = true
)
@CompoundIndex(
        name = "venue_row_col_sort",
        def = "{'venueId': 1, 'seatRow': 1, 'seatCol': 1}"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Seat {

    @Id
    private String id;

    private String venueId;

    private String seatRow;

    private Integer seatCol;

    private String seatNumber;

    private String seatType;

    private BigDecimal price;

    @Builder.Default
    private Boolean active = Boolean.TRUE;
}
