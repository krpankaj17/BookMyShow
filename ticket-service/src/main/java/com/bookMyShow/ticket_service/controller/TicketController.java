package com.bookMyShow.ticket_service.controller;

import com.bookMyShow.ticket_service.dto.TicketIssueRequest;
import com.bookMyShow.ticket_service.dto.TicketResponse;
import com.bookMyShow.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/issue")
    public ResponseEntity<TicketResponse> issueTicket(@RequestBody @Valid TicketIssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ticketService.issueTicket(
                        request.bookingId(),
                        request.userId(),
                        request.showId(),
                        request.seatNumbers(),
                        request.totalAmount()
                )
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<TicketResponse> getTicketByBookingId(@PathVariable String bookingId) {
        return ResponseEntity.status(HttpStatus.OK).body(ticketService.getTicketByBookingId(bookingId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketResponse>> getUserTickets(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(ticketService.getUserTickets(userId));
    }
}
