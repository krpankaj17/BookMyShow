package com.bookMyShow.booking_service.booking.dto;

import com.bookMyShow.booking_service.booking.enums.SeatStatus;

import java.math.BigDecimal;

public record SeatAvailabilityResponse(
        String seatNumber,
        String seatRow,
        Integer seatCol,
        String seatType,
        BigDecimal price,
        SeatStatus status
) {}
