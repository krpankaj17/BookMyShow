
package com.bookMyShow.booking_service.catalog.controller;

import com.bookMyShow.booking_service.catalog.dto.SeatLayoutRequest;
import com.bookMyShow.booking_service.catalog.entity.Seat;
import com.bookMyShow.booking_service.catalog.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping("/generate-layout")
    public ResponseEntity<List<Seat>> generateSeatLayout(
            @Valid @RequestBody SeatLayoutRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seatService.generateSeatLayout(request));
    }

    @GetMapping("/by-venue/{venueId}")
    public ResponseEntity<List<Seat>> getSeatsByVenue(
            @PathVariable String venueId) {

        return ResponseEntity.ok(
                seatService.getSeatsByVenue(venueId));
    }
}
