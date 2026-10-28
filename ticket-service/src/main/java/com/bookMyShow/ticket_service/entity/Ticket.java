package com.bookMyShow.ticket_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {

    @Id
    private String id;

    @Indexed(unique = true)
    private String ticketNumber;

    @Indexed(unique = true)
    private String bookingId;

    @Indexed
    private String userId;

    private String showId;

    private List<String> seatNumbers;

    private BigDecimal totalAmount;

    @Builder.Default
    private String status = "ISSUED";

    private LocalDateTime issuedAt;
}
