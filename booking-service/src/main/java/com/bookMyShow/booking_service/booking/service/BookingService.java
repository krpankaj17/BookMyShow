package com.bookMyShow.booking_service.booking.service;

import com.bookMyShow.booking_service.booking.dto.BookingHoldData;
import com.bookMyShow.booking_service.booking.dto.HoldSeatRequest;
import com.bookMyShow.booking_service.booking.dto.SeatAvailabilityResponse;
import com.bookMyShow.booking_service.booking.entity.Booking;
import com.bookMyShow.booking_service.booking.enums.BookingStatus;
import com.bookMyShow.booking_service.booking.enums.SeatStatus;
import com.bookMyShow.booking_service.booking.repository.BookingRepository;
import com.bookMyShow.booking_service.catalog.entity.Seat;
import com.bookMyShow.booking_service.catalog.entity.Show;
import com.bookMyShow.booking_service.catalog.repository.SeatRepository;
import com.bookMyShow.booking_service.catalog.repository.ShowRepository;
import com.bookMyShow.booking_service.common.exception.ResourceNotFoundException;
import com.bookMyShow.booking_service.messaging.event.HoldQueueMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class    BookingService {

    private final BookingRepository bookingRepository;
    private final SeatHoldService seatHoldService;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public BookingService(
            BookingRepository bookingRepository,
            SeatHoldService seatHoldService,
            ShowRepository showRepository,
            SeatRepository seatRepository,
            @Autowired(required = false) KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.bookingRepository = bookingRepository;
        this.seatHoldService = seatHoldService;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public List<SeatAvailabilityResponse> getSeatAvailability(String showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showId));

        if (!Boolean.TRUE.equals(show.getActive())) {
            throw new IllegalArgumentException("Show is not active");
        }

        List<Seat> seats = seatRepository.findByVenueIdAndActiveTrueOrderBySeatRowAscSeatColAsc(show.getVenueId());

        Set<String> bookedSeats;
        if (seatHoldService.hasBookedSeatsCache(showId)) {
            bookedSeats = seatHoldService.getBookedSeats(showId);
        } else {
            List<Booking> confirmedBookings = bookingRepository.findByShowIdAndStatus(showId, BookingStatus.CONFIRMED);
            bookedSeats = confirmedBookings.stream()
                    .flatMap(b -> b.getSeatNumbers().stream())
                    .collect(Collectors.toSet());
            seatHoldService.setBookedSeats(showId, bookedSeats);
        }

        Set<String> heldSeats = seatHoldService.getHeldSeats(showId);

        return seats.stream()
                .map(seat -> {
                    SeatStatus status;
                    if (bookedSeats.contains(seat.getSeatNumber())) {
                        status = SeatStatus.BOOKED;
                    } else if (heldSeats.contains(seat.getSeatNumber())) {
                        status = SeatStatus.HELD;
                    } else {
                        status = SeatStatus.AVAILABLE;
                    }
                    return new SeatAvailabilityResponse(
                            seat.getSeatNumber(),
                            seat.getSeatRow(),
                            seat.getSeatCol(),
                            seat.getSeatType(),
                            seat.getPrice(),
                            status
                    );
                })
                .toList();
    }

    public BookingHoldData enqueueHold(HoldSeatRequest request) {
        if (request.seatNumbers() == null || request.seatNumbers().isEmpty()) {
            throw new IllegalArgumentException("Seat numbers cannot be empty");
        }

        String bookingId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        BookingHoldData initialData = new BookingHoldData(
                bookingId,
                request.userId(),
                request.showId(),
                request.seatNumbers(),
                BigDecimal.ZERO,
                "PROCESSING",
                null,
                expiresAt
        );
        seatHoldService.saveHoldData(initialData, 300);

        HoldQueueMessage queueMessage = new HoldQueueMessage(
                bookingId,
                request.userId(),
                request.showId(),
                request.seatNumbers(),
                LocalDateTime.now()
        );

        publishBookingEvent("booking-initiate", request.showId(), queueMessage);

        return initialData;
    }

    public void processHoldQueueMessage(HoldQueueMessage message) {
        Optional<Show> showOpt = showRepository.findById(message.showId());
        if (showOpt.isEmpty() || !Boolean.TRUE.equals(showOpt.get().getActive()) || showOpt.get().getStartTime().isBefore(LocalDateTime.now())) {
            seatHoldService.saveHoldData(
                    new BookingHoldData(message.bookingId(), message.userId(), message.showId(), message.seatNumbers(), BigDecimal.ZERO, "REJECTED", "Show is invalid or inactive", null),
                    60
            );
            return;
        }

        Show show = showOpt.get();
        List<Seat> venueSeats = seatRepository.findByVenueIdAndActiveTrueOrderBySeatRowAscSeatColAsc(show.getVenueId());
        Map<String, Seat> seatMap = venueSeats.stream()
                .collect(Collectors.toMap(Seat::getSeatNumber, Function.identity()));

        for (String seatNumber : message.seatNumbers()) {
            if (!seatMap.containsKey(seatNumber)) {
                seatHoldService.saveHoldData(
                        new BookingHoldData(message.bookingId(), message.userId(), message.showId(), message.seatNumbers(), BigDecimal.ZERO, "REJECTED", "Seat " + seatNumber + " does not exist in venue", null),
                        60
                );
                return;
            }
        }

        if (seatHoldService.hasBookedSeatsCache(message.showId())) {
            Set<String> booked = seatHoldService.getBookedSeats(message.showId());
            for (String seatNumber : message.seatNumbers()) {
                if (booked.contains(seatNumber)) {
                    seatHoldService.saveHoldData(
                            new BookingHoldData(message.bookingId(), message.userId(), message.showId(), message.seatNumbers(), BigDecimal.ZERO, "REJECTED", "One or more seats are already booked", null),
                            60
                    );
                    return;
                }
            }
        } else {
            boolean alreadyBooked = bookingRepository.existsByShowIdAndSeatsAndStatus(
                    message.showId(),
                    message.seatNumbers(),
                    BookingStatus.CONFIRMED
            );
            if (alreadyBooked) {
                seatHoldService.saveHoldData(
                        new BookingHoldData(message.bookingId(), message.userId(), message.showId(), message.seatNumbers(), BigDecimal.ZERO, "REJECTED", "One or more seats are already booked", null),
                        60
                );
                return;
            }
        }

        boolean acquired = seatHoldService.acquireHold(
                message.showId(),
                message.seatNumbers(),
                message.bookingId(),
                300
        );

        if (!acquired) {
            seatHoldService.saveHoldData(
                    new BookingHoldData(message.bookingId(), message.userId(), message.showId(), message.seatNumbers(), BigDecimal.ZERO, "REJECTED", "One or more seats were held by another user", null),
                    60
            );
            return;
        }

        BigDecimal totalAmount = message.seatNumbers().stream()
                .map(seatNumber -> seatMap.get(seatNumber).getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        BookingHoldData heldData = new BookingHoldData(
                message.bookingId(),
                message.userId(),
                message.showId(),
                message.seatNumbers(),
                totalAmount,
                "HELD",
                null,
                expiresAt
        );

        seatHoldService.saveHoldData(heldData, 300);
    }

    public BookingHoldData getHoldStatus(String bookingId) {
        BookingHoldData holdData = seatHoldService.getHoldData(bookingId);
        if (holdData != null) {
            return holdData;
        }

        return bookingRepository.findById(bookingId)
                .map(b -> new BookingHoldData(
                        b.getId(),
                        b.getUserId(),
                        b.getShowId(),
                        b.getSeatNumbers(),
                        b.getTotalAmount(),
                        b.getStatus().name(),
                        null,
                        b.getExpiresAt()
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Booking hold not found with id: " + bookingId));
    }

    public Booking confirmBooking(String bookingId) {
        Optional<Booking> existing = bookingRepository.findById(bookingId);
        if (existing.isPresent()) {
            return existing.get();
        }

        BookingHoldData holdData = seatHoldService.getHoldData(bookingId);
        if (holdData == null || !"HELD".equals(holdData.status())) {
            throw new IllegalStateException("Booking hold has expired or was not secured");
        }

        if (holdData.expiresAt() != null && LocalDateTime.now().isAfter(holdData.expiresAt())) {
            seatHoldService.releaseHold(holdData.showId(), holdData.seatNumbers(), bookingId);
            seatHoldService.removeHoldData(bookingId);
            throw new IllegalStateException("Booking hold has expired");
        }

        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .id(bookingId)
                .userId(holdData.userId())
                .showId(holdData.showId())
                .seatNumbers(holdData.seatNumbers())
                .totalAmount(holdData.totalAmount())
                .status(BookingStatus.CONFIRMED)
                .expiresAt(holdData.expiresAt())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Booking confirmedBooking = bookingRepository.save(booking);

        seatHoldService.addBookedSeats(
                confirmedBooking.getShowId(),
                confirmedBooking.getSeatNumbers()
        );

        seatHoldService.releaseHold(
                confirmedBooking.getShowId(),
                confirmedBooking.getSeatNumbers(),
                bookingId
        );
        seatHoldService.removeHoldData(bookingId);

        publishBookingEvent("booking-confirmed", confirmedBooking.getShowId(), confirmedBooking);

        return confirmedBooking;
    }

    public Booking cancelBooking(String bookingId) {
        BookingHoldData holdData = seatHoldService.getHoldData(bookingId);
        if (holdData != null) {
            seatHoldService.releaseHold(holdData.showId(), holdData.seatNumbers(), bookingId);
            seatHoldService.removeHoldData(bookingId);
            Booking cancelled = Booking.builder()
                    .id(bookingId)
                    .userId(holdData.userId())
                    .showId(holdData.showId())
                    .seatNumbers(holdData.seatNumbers())
                    .totalAmount(holdData.totalAmount())
                    .status(BookingStatus.CANCELLED)
                    .build();
            publishBookingEvent("booking-cancelled", holdData.showId(), cancelled);
            return cancelled;
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking;
        }

        seatHoldService.removeBookedSeats(
                booking.getShowId(),
                booking.getSeatNumbers()
        );

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        Booking cancelledBooking = bookingRepository.save(booking);

        publishBookingEvent("booking-cancelled", cancelledBooking.getShowId(), cancelledBooking);

        return cancelledBooking;
    }

    public Booking getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private void publishBookingEvent(String topic, String key, Object payload) {
        if (kafkaTemplate != null) {
            try {
                String message = objectMapper.writeValueAsString(payload);
                kafkaTemplate.send(topic, key, message);
            } catch (Exception ignored) {
            }
        }
    }
}
