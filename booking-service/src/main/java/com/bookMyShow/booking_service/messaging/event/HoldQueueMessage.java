package com.bookMyShow.booking_service.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public record HoldQueueMessage(
        String bookingId,
        String userId,
        String showId,
        List<String> seatNumbers,
        LocalDateTime requestedAt
) {}
