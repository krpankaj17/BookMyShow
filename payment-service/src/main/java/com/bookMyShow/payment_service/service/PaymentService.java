package com.bookMyShow.payment_service.service;

import com.bookMyShow.payment_service.dto.PaymentRequest;
import com.bookMyShow.payment_service.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class PaymentService {

    private final RestClient restClient;
    private final String bookingServiceUrl;

    public PaymentService(@Value("${booking.service.url:http://localhost:8082}") String bookingServiceUrl) {
        this.bookingServiceUrl = bookingServiceUrl;
        this.restClient = RestClient.create();
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        restClient.post()
                .uri(bookingServiceUrl + "/api/bookings/{id}/confirm", request.bookingId())
                .retrieve()
                .toBodilessEntity();

        return new PaymentResponse(
                paymentId,
                request.bookingId(),
                request.userId(),
                request.amount(),
                "SUCCESS",
                transactionId,
                "Payment processed successfully and booking confirmed"
        );
    }
}
