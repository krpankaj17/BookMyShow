package com.bookMyShow.booking_service.catalog.repository;

import com.bookMyShow.booking_service.catalog.entity.Seat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SeatRepository extends MongoRepository<Seat, String> {

    List<Seat> findByVenueIdAndActiveTrueOrderBySeatRowAscSeatColAsc(
            String venueId
    );

    boolean existsByVenueId(String venueId);
}
