package com.bookMyShow.ticket_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TicketResponse(
        String ticketNumber,
        String bookingId,
        String userId,
        String showId,
        List<String> seatNumbers,
        BigDecimal totalAmount,
        String status,
        LocalDateTime issuedAt
) {}
