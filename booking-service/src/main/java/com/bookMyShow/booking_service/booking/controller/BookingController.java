package com.bookMyShow.booking_service.booking.controller;

import com.bookMyShow.booking_service.booking.dto.BookingHoldData;
import com.bookMyShow.booking_service.booking.dto.HoldSeatRequest;
import com.bookMyShow.booking_service.booking.dto.SeatAvailabilityResponse;
import com.bookMyShow.booking_service.booking.entity.Booking;
import com.bookMyShow.booking_service.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/shows/{showId}/availability")
    public ResponseEntity<List<SeatAvailabilityResponse>> getSeatAvailability(@PathVariable String showId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getSeatAvailability(showId));
    }

    @PostMapping("/hold")
    public ResponseEntity<BookingHoldData> holdSeats(@RequestBody @Valid HoldSeatRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(bookingService.enqueueHold(request));
    }

    @GetMapping("/hold/{bookingId}/status")
    public ResponseEntity<BookingHoldData> getHoldStatus(@PathVariable String bookingId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getHoldStatus(bookingId));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.confirmBooking(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.cancelBooking(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBookingById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getUserBookings(userId));
    }
}
