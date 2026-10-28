package com.bookMyShow.booking_service.config;

import com.bookMyShow.booking_service.catalog.entity.Movie;
import com.bookMyShow.booking_service.catalog.entity.Seat;
import com.bookMyShow.booking_service.catalog.entity.Show;
import com.bookMyShow.booking_service.catalog.entity.Venue;
import com.bookMyShow.booking_service.catalog.repository.MovieRepository;
import com.bookMyShow.booking_service.catalog.repository.SeatRepository;
import com.bookMyShow.booking_service.catalog.repository.ShowRepository;
import com.bookMyShow.booking_service.catalog.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;

    public DataInitializer(
            MovieRepository movieRepository,
            VenueRepository venueRepository,
            SeatRepository seatRepository,
            ShowRepository showRepository
    ) {
        this.movieRepository = movieRepository;
        this.venueRepository = venueRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
    }

    @Override
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            return;
        }

        Movie movie = Movie.builder()
                .title("Inception")
                .description("A thief who steals corporate secrets through dream-sharing technology.")
                .genre("Sci-Fi")
                .language("English")
                .durationMinutes(148)
                .active(true)
                .build();
        Movie savedMovie = movieRepository.save(movie);

        Venue venue = Venue.builder()
                .name("PVR Cinemas, Audi 1")
                .city("Mumbai")
                .address("Phoenix Marketcity, Kurla")
                .active(true)
                .build();
        Venue savedVenue = venueRepository.save(venue);

        List<Seat> seats = new ArrayList<>();
        String[] rows = {"A", "B", "C"};
        for (String row : rows) {
            String seatType = "A".equals(row) ? "VIP" : ("B".equals(row) ? "PREMIUM" : "CLASSIC");
            BigDecimal price = "A".equals(row) ? new BigDecimal("300.00") : ("B".equals(row) ? new BigDecimal("200.00") : new BigDecimal("150.00"));

            for (int col = 1; col <= 10; col++) {
                seats.add(Seat.builder()
                        .venueId(savedVenue.getId())
                        .seatRow(row)
                        .seatCol(col)
                        .seatNumber(row + col)
                        .seatType(seatType)
                        .price(price)
                        .active(true)
                        .build());
            }
        }
        seatRepository.saveAll(seats);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = startTime.plusMinutes(150);

        Show show = Show.builder()
                .movieId(savedMovie.getId())
                .venueId(savedVenue.getId())
                .startTime(startTime)
                .endTime(endTime)
                .language("English")
                .active(true)
                .build();
        showRepository.save(show);
    }
}
