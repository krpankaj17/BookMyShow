package com.bookMyShow.booking_service.booking.dto;

import com.bookMyShow.booking_service.booking.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record HoldSeatResponse(
        String bookingId,
        String userId,
        String showId,
        List<String> seatNumbers,
        BigDecimal totalAmount,
        BookingStatus status,
        LocalDateTime expiresAt
) {}
