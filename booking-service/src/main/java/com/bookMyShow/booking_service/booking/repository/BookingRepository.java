package com.bookMyShow.booking_service.booking.repository;

import com.bookMyShow.booking_service.booking.entity.Booking;
import com.bookMyShow.booking_service.booking.enums.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByShowIdAndStatus(String showId, BookingStatus status);

    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, LocalDateTime dateTime);

    @Query(value = "{ 'showId': ?0, 'seatNumbers': { $in: ?1 }, 'status': ?2 }", exists = true)
    boolean existsByShowIdAndSeatsAndStatus(String showId, List<String> seatNumbers, BookingStatus status);
}
