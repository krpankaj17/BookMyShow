package com.bookMyShow.booking_service.catalog.service;

import com.bookMyShow.booking_service.catalog.dto.MovieRequest;
import com.bookMyShow.booking_service.catalog.dto.MovieResponse;
import com.bookMyShow.booking_service.catalog.dto.MovieUpdateRequest;
import com.bookMyShow.booking_service.catalog.entity.Movie;
import com.bookMyShow.booking_service.catalog.repository.MovieRepository;
import com.bookMyShow.booking_service.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class MovieService {
    private final MovieRepository movieRepository;
    public MovieService(MovieRepository movieRepository){
        this.movieRepository=movieRepository;
    }

    public Page<MovieResponse>  getAllMovies(Pageable pageable){
        return movieRepository.findAll(pageable).map(this::mapToResponse );
    }
    public MovieResponse createMovie(MovieRequest movieRequest){
        Movie movie = Movie.builder()
                .title(movieRequest.title())
                .description(movieRequest.description())
                .genre(movieRequest.genre())
                .language(movieRequest.language())
                .durationMinutes(movieRequest.durationMinutes())
                .active(Boolean.TRUE)
                .build();
        Movie savedMovie = movieRepository.save(movie);
        return mapToResponse(savedMovie);
    }

    public MovieResponse updateMovie(MovieUpdateRequest request, String id){
        Movie movie = movieRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Movie Not found with Id "+id));
        if (request.title()!=null){
            movie.setTitle(request.title());
        }
        if (request.description()!=null){
            movie.setDescription(request.description());
        }
        if (request.genre()!=null){
            movie.setGenre(request.genre());
        }
        if (request.language()!=null){
            movie.setLanguage(request.language());
        }
        if (request.durationMinutes()!=null){
            movie.setDurationMinutes(request.durationMinutes());
        }
        if (request.active()!=null){
            movie.setActive(request.active());
        }

        return mapToResponse(movieRepository.save(movie));
    }


    public void softDeleteMovie(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Movie not found with id " + id));

        movie.setActive(Boolean.FALSE);
        movieRepository.save(movie);
    }

    public MovieResponse findMovieById(String id){
        Movie movie = movieRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Movie not found with id "+id));
        return mapToResponse(movie);
    }

    private MovieResponse mapToResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getGenre(),
                movie.getLanguage(),
                movie.getDurationMinutes(),
                movie.getActive()
        );
    }


}
