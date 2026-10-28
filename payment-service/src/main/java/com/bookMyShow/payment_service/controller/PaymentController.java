package com.bookMyShow.payment_service.controller;

import com.bookMyShow.payment_service.dto.PaymentRequest;
import com.bookMyShow.payment_service.dto.PaymentResponse;
import com.bookMyShow.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody @Valid PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.processPayment(request));
    }
}
