package com.bookMyShow.booking_service.catalog.repository;

import com.bookMyShow.booking_service.catalog.entity.Venue;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VenueRepository extends MongoRepository<Venue,String> {
}
