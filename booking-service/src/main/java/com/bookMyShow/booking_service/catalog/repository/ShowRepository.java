package com.bookMyShow.booking_service.catalog.repository;

import com.bookMyShow.booking_service.catalog.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowRepository extends MongoRepository<Show, String> {

    Page<Show> findByActiveTrue(Pageable pageable);

    @Query("{ 'venueId': ?0, 'active': true, 'startTime': { $lt: ?2 }, 'endTime': { $gt: ?1 } }")
    List<Show> findOverlappingShows(String venueId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("{ 'venueId': ?0, 'active': true, 'id': { $ne: ?3 }, 'startTime': { $lt: ?2 }, 'endTime': { $gt: ?1 } }")
    List<Show> findOverlappingShowsExcludingId(String venueId, LocalDateTime startTime, LocalDateTime endTime, String showId);
}

