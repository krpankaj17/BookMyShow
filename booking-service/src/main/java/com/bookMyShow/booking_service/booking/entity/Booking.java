package com.bookMyShow.booking_service.booking.entity;

import com.bookMyShow.booking_service.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "bookings")
@CompoundIndex(
        name = "show_status_idx",
        def = "{'showId': 1, 'status': 1}"
)
@CompoundIndex(
        name = "user_created_idx",
        def = "{'userId': 1, 'createdAt': -1}"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {

    @Id
    private String id;

    private String userId;

    private String showId;

    private List<String> seatNumbers;

    private BigDecimal totalAmount;

    private BookingStatus status;

    private String paymentId;

    private LocalDateTime expiresAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
