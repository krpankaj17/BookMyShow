package com.bookMyShow.ticket_service.repository;

import com.bookMyShow.ticket_service.entity.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    Optional<Ticket> findByBookingId(String bookingId);

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByUserIdOrderByIssuedAtDesc(String userId);

    boolean existsByBookingId(String bookingId);
}
