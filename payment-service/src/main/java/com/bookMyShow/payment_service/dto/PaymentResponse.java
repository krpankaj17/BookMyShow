package com.bookMyShow.payment_service.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        String paymentId,
        String bookingId,
        String userId,
        BigDecimal amount,
        String status,
        String transactionId,
        String message
) {}
