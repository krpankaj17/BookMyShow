
package com.bookMyShow.booking_service.catalog.service;

import com.bookMyShow.booking_service.catalog.dto.SeatLayoutRequest;
import com.bookMyShow.booking_service.catalog.entity.Seat;
import com.bookMyShow.booking_service.catalog.entity.Venue;
import com.bookMyShow.booking_service.catalog.repository.SeatRepository;
import com.bookMyShow.booking_service.catalog.repository.VenueRepository;
import com.bookMyShow.booking_service.common.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;

    public SeatService(
            SeatRepository seatRepository,
            VenueRepository venueRepository) {
        this.seatRepository = seatRepository;
        this.venueRepository = venueRepository;
    }

    public List<Seat> generateSeatLayout(SeatLayoutRequest request) {

        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Venue not found with id " + request.venueId()
                        ));

        if (!Boolean.TRUE.equals(venue.getActive())) {
            throw new IllegalArgumentException("Venue is inactive");
        }

        if (seatRepository.existsByVenueId(venue.getId())) {
            throw new IllegalArgumentException(
                    "Seat layout already exists for this venue"
            );
        }

        List<Seat> seats = new ArrayList<>();

        for (int row = 0; row < request.numberOfRows(); row++) {

            char rowLetter = (char) ('A' + row);

            for (int seatNumber = 1;
                 seatNumber <= request.seatsPerRow();
                 seatNumber++) {

                String seatLabel = rowLetter + String.valueOf(seatNumber);

                Seat seat = Seat.builder()
                        .venueId(venue.getId())
                        .seatRow(String.valueOf(rowLetter))
                        .seatCol(seatNumber)
                        .seatNumber(seatLabel)
                        .seatType("REGULAR")
                        .price(BigDecimal.valueOf(200.0))
                        .active(true)
                        .build();

                seats.add(seat);
            }
        }

        return seatRepository.saveAll(seats);
    }

    public List<Seat> getSeatsByVenue(String venueId) {

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Venue not found with id " + venueId
                        ));

        return seatRepository
                .findByVenueIdAndActiveTrueOrderBySeatRowAscSeatColAsc(
                        venue.getId()
                );
    }
}
