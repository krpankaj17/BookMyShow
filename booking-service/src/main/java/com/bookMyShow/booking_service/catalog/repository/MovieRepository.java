package com.bookMyShow.booking_service.catalog.repository;

import com.bookMyShow.booking_service.catalog.entity.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie,String> {
}
