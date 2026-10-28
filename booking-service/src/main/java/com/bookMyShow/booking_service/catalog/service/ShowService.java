package com.bookMyShow.booking_service.catalog.service;

import com.bookMyShow.booking_service.catalog.dto.ShowRequest;
import com.bookMyShow.booking_service.catalog.dto.ShowResponse;
import com.bookMyShow.booking_service.catalog.dto.ShowUpdateRequest;
import com.bookMyShow.booking_service.catalog.entity.Movie;
import com.bookMyShow.booking_service.catalog.entity.Show;
import com.bookMyShow.booking_service.catalog.entity.Venue;
import com.bookMyShow.booking_service.catalog.repository.MovieRepository;
import com.bookMyShow.booking_service.catalog.repository.ShowRepository;
import com.bookMyShow.booking_service.catalog.repository.VenueRepository;
import com.bookMyShow.booking_service.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShowService {

    private final MovieRepository movieRepository;
    private final VenueRepository venueRepository;
    private final ShowRepository showRepository;

    public ShowService(MovieRepository movieRepository,
                       VenueRepository venueRepository,
                       ShowRepository showRepository) {
        this.movieRepository = movieRepository;
        this.venueRepository = venueRepository;
        this.showRepository = showRepository;
    }


    public ShowResponse createShow(ShowRequest request) {
        Movie movie = getMovie(request.movieId());
        Venue venue = getVenue(request.venueId());

        if (!Boolean.TRUE.equals(movie.getActive()) || !Boolean.TRUE.equals(venue.getActive())) {
            throw new IllegalArgumentException("Movie or Venue is inactive");
        }
        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException("Show end time must be after start time");
        }

        validateNoOverlap(venue.getId(), request.startTime(), request.endTime(), null);

        Show show = Show.builder()
                .movieId(movie.getId())
                .venueId(venue.getId())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .language(request.language())
                .active(true)
                .build();

        return mapToResponse(showRepository.save(show), movie, venue);
    }

  
    public Page<ShowResponse> getAllShows(Pageable pageable) {
        Page<Show> showsPage = showRepository.findByActiveTrue(pageable);
        if (showsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<String> movieIds = showsPage.map(Show::getMovieId).toList();
        List<String> venueIds = showsPage.map(Show::getVenueId).toList();

        Map<String, Movie> movieMap = new HashMap<>();
        for (Movie m : movieRepository.findAllById(movieIds)) {
            movieMap.put(m.getId(), m);
        }

        Map<String, Venue> venueMap = new HashMap<>();
        for (Venue v : venueRepository.findAllById(venueIds)) {
            venueMap.put(v.getId(), v);
        }

        return showsPage.map(show ->
                mapToResponse(show, movieMap.get(show.getMovieId()), venueMap.get(show.getVenueId()))
        );
    }

    public ShowResponse updateShow(ShowUpdateRequest request, String id) {
        Show show = getShow(id);

        if (request.movieId() != null) {
            Movie movie = getMovie(request.movieId());
            if (!Boolean.TRUE.equals(movie.getActive())) {
                throw new IllegalArgumentException("Movie is inactive");
            }
            show.setMovieId(movie.getId());
        }

        if (request.venueId() != null) {
            Venue venue = getVenue(request.venueId());
            if (!Boolean.TRUE.equals(venue.getActive())) {
                throw new IllegalArgumentException("Venue is inactive");
            }
            show.setVenueId(venue.getId());
        }

        if (request.startTime() != null) show.setStartTime(request.startTime());
        if (request.endTime() != null) show.setEndTime(request.endTime());
        if (request.language() != null) show.setLanguage(request.language());
        if (request.active() != null) show.setActive(request.active());

        if (!show.getEndTime().isAfter(show.getStartTime())) {
            throw new IllegalArgumentException("Show end time must be after start time");
        }

        if (Boolean.TRUE.equals(show.getActive())) {
            validateNoOverlap(show.getVenueId(), show.getStartTime(), show.getEndTime(), show.getId());
        }

        Show saved = showRepository.save(show);
        return mapToResponse(saved, getMovie(saved.getMovieId()), getVenue(saved.getVenueId()));
    }

   
    public ShowResponse getShowById(String id) {
        Show show = getShow(id);
        return mapToResponse(show, getMovie(show.getMovieId()), getVenue(show.getVenueId()));
    }

  
    public void softDeleteShow(String id) {
        Show show = getShow(id);
        show.setActive(false);
        showRepository.save(show);
    }



    private Movie getMovie(String id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + id));
    }

    private Venue getVenue(String id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id " + id));
    }

    private Show getShow(String id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + id));
    }

    private void validateNoOverlap(String venueId, LocalDateTime start, LocalDateTime end, String excludeId) {
        List<Show> conflicts = (excludeId == null)
                ? showRepository.findOverlappingShows(venueId, start, end)
                : showRepository.findOverlappingShowsExcludingId(venueId, start, end, excludeId);

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Another show is already scheduled at this venue during this time window");
        }
    }

    private ShowResponse mapToResponse(Show show, Movie movie, Venue venue) {
        ShowResponse.MovieSummary movieSummary = (movie != null)
                ? new ShowResponse.MovieSummary(movie.getId(), movie.getTitle(), movie.getGenre(), movie.getDurationMinutes())
                : new ShowResponse.MovieSummary(show.getMovieId(), "Unknown", "Unknown", 0);

        ShowResponse.VenueSummary venueSummary = (venue != null)
                ? new ShowResponse.VenueSummary(venue.getId(), venue.getName(), venue.getAddress(), venue.getCity())
                : new ShowResponse.VenueSummary(show.getVenueId(), "Unknown", "Unknown", "Unknown");

        return new ShowResponse(
                show.getId(),
                movieSummary,
                venueSummary,
                show.getStartTime(),
                show.getEndTime(),
                show.getLanguage(),
                show.getActive()
        );
    }
}
