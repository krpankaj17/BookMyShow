package com.bookMyShow.ticket_service.service;

import com.bookMyShow.ticket_service.dto.TicketResponse;
import com.bookMyShow.ticket_service.entity.Ticket;
import com.bookMyShow.ticket_service.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public TicketResponse issueTicket(
            String bookingId,
            String userId,
            String showId,
            List<String> seatNumbers,
            BigDecimal totalAmount
    ) {
        return ticketRepository.findByBookingId(bookingId)
                .map(this::mapToResponse)
                .orElseGet(() -> {
                    String ticketNumber = "TKT-" + LocalDateTime.now().getYear() + "-"
                            + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                    Ticket ticket = Ticket.builder()
                            .ticketNumber(ticketNumber)
                            .bookingId(bookingId)
                            .userId(userId)
                            .showId(showId)
                            .seatNumbers(seatNumbers)
                            .totalAmount(totalAmount)
                            .status("ISSUED")
                            .issuedAt(LocalDateTime.now())
                            .build();

                    Ticket saved = ticketRepository.save(ticket);
                    return mapToResponse(saved);
                });
    }

    public TicketResponse getTicketByBookingId(String bookingId) {
        return ticketRepository.findByBookingId(bookingId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found for booking id: " + bookingId));
    }

    public List<TicketResponse> getUserTickets(String userId) {
        return ticketRepository.findByUserIdOrderByIssuedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getTicketNumber(),
                ticket.getBookingId(),
                ticket.getUserId(),
                ticket.getShowId(),
                ticket.getSeatNumbers(),
                ticket.getTotalAmount(),
                ticket.getStatus(),
                ticket.getIssuedAt()
        );
    }
}
