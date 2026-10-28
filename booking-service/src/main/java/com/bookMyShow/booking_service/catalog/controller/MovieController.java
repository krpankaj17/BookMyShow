package com.bookMyShow.booking_service.catalog.controller;

import com.bookMyShow.booking_service.catalog.dto.MovieRequest;
import com.bookMyShow.booking_service.catalog.dto.MovieResponse;
import com.bookMyShow.booking_service.catalog.dto.MovieUpdateRequest;
import com.bookMyShow.booking_service.catalog.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;
    public MovieController(MovieService movieService){
        this.movieService=movieService;
    }
    @GetMapping
    public ResponseEntity<Page<MovieResponse>> getAllMovies(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }
    @PostMapping
    public ResponseEntity<MovieResponse>  createMovie(@RequestBody @Valid MovieRequest movieRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(movieRequest));
    }
    @PatchMapping ("/{id}")
    public ResponseEntity<MovieResponse>  updateMovie(@RequestBody @Valid MovieUpdateRequest request, @PathVariable  String id){
        return ResponseEntity.status(HttpStatus.OK).body(movieService.updateMovie(request,id));
    }
    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String id){
        movieService.softDeleteMovie(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> findMovieById(@PathVariable String id){
       return ResponseEntity.status(HttpStatus.OK).body(movieService.findMovieById(id));
    }

}
