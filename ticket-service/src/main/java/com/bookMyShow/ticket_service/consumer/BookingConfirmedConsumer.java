package com.bookMyShow.ticket_service.consumer;

import com.bookMyShow.ticket_service.service.TicketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingConfirmedConsumer {

    private final TicketService ticketService;
    private final ObjectMapper objectMapper;

    public BookingConfirmedConsumer(TicketService ticketService, ObjectMapper objectMapper) {
        this.ticketService = ticketService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "booking-confirmed", groupId = "ticket-service-group")
    public void consume(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String bookingId = node.path("id").asText(null);
            if (bookingId == null || bookingId.isBlank()) {
                bookingId = node.path("bookingId").asText();
            }
            String userId = node.path("userId").asText();
            String showId = node.path("showId").asText();
            BigDecimal totalAmount = new BigDecimal(node.path("totalAmount").asText("0"));

            List<String> seatNumbers = new ArrayList<>();
            JsonNode seatsNode = node.path("seatNumbers");
            if (seatsNode.isArray()) {
                for (JsonNode seat : seatsNode) {
                    seatNumbers.add(seat.asText());
                }
            }

            ticketService.issueTicket(bookingId, userId, showId, seatNumbers, totalAmount);
        } catch (Exception ignored) {
        }
    }
}
