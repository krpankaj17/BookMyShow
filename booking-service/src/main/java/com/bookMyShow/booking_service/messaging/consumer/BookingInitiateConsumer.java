package com.bookMyShow.booking_service.messaging.consumer;

import com.bookMyShow.booking_service.booking.service.BookingService;
import com.bookMyShow.booking_service.messaging.event.HoldQueueMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingInitiateConsumer {

    private final BookingService bookingService;
    private final ObjectMapper objectMapper;

    public BookingInitiateConsumer(BookingService bookingService, ObjectMapper objectMapper) {
        this.bookingService = bookingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "booking-initiate", groupId = "booking-initiate-group")
    public void consume(String message) {
        try {
            HoldQueueMessage queueMessage = objectMapper.readValue(message, HoldQueueMessage.class);
            bookingService.processHoldQueueMessage(queueMessage);
        } catch (Exception e) {
            throw new RuntimeException("Booking initiation message processing failed", e);
        }
    }
}
