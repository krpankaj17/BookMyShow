package com.bookMyShow.booking_service.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingHoldData(
        String bookingId,
        String userId,
        String showId,
        List<String> seatNumbers,
        BigDecimal totalAmount,
        String status,
        String failureReason,
        LocalDateTime expiresAt
) {}
