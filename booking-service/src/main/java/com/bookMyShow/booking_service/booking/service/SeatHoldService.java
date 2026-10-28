package com.bookMyShow.booking_service.booking.service;

import com.bookMyShow.booking_service.booking.dto.BookingHoldData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SeatHoldService {

    private static final String HOLD_PREFIX = "hold:";
    private static final String BOOKED_PREFIX = "booked:";
    private static final String HOLD_BOOKING_PREFIX = "booking:hold:";
    private static final String EMPTY_MARKER = "__EMPTY__";
    private static final Duration BOOKED_CACHE_TTL = Duration.ofHours(24);

    private static final String ACQUIRE_HOLD_LUA =
            "for i, key in ipairs(KEYS) do " +
            "  if redis.call('EXISTS', key) == 1 then " +
            "    return 0 " +
            "  end " +
            "end " +
            "for i, key in ipairs(KEYS) do " +
            "  redis.call('SET', key, ARGV[1], 'EX', ARGV[2]) " +
            "end " +
            "return 1";

    private static final String RELEASE_HOLD_LUA =
            "for i, key in ipairs(KEYS) do " +
            "  if redis.call('GET', key) == ARGV[1] then " +
            "    redis.call('DEL', key) " +
            "  end " +
            "end " +
            "return 1";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final DefaultRedisScript<Long> holdScript;
    private final DefaultRedisScript<Long> releaseScript;

    public SeatHoldService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.holdScript = new DefaultRedisScript<>(ACQUIRE_HOLD_LUA, Long.class);
        this.releaseScript = new DefaultRedisScript<>(RELEASE_HOLD_LUA, Long.class);
    }

    public boolean acquireHold(String showId, List<String> seatNumbers, String bookingId, long ttlSeconds) {
        if (seatNumbers == null || seatNumbers.isEmpty()) {
            return false;
        }

        List<String> keys = seatNumbers.stream()
                .map(seat -> buildKey(showId, seat))
                .toList();

        Long result = redisTemplate.execute(
                holdScript,
                keys,
                bookingId,
                String.valueOf(ttlSeconds)
        );

        return Long.valueOf(1L).equals(result);
    }

    public void releaseHold(String showId, List<String> seatNumbers, String bookingId) {
        if (seatNumbers == null || seatNumbers.isEmpty() || bookingId == null) {
            return;
        }

        List<String> keys = seatNumbers.stream()
                .map(seat -> buildKey(showId, seat))
                .toList();

        redisTemplate.execute(
                releaseScript,
                keys,
                bookingId
        );
    }

    public Set<String> getHeldSeats(String showId) {
        ScanOptions options = ScanOptions.scanOptions()
                .match(HOLD_PREFIX + showId + ":*")
                .count(100)
                .build();

        Set<String> heldSeats = new HashSet<>();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                heldSeats.add(key.substring(key.lastIndexOf(':') + 1));
            }
        }

        return heldSeats;
    }

    public void saveHoldData(BookingHoldData data, long ttlSeconds) {
        try {
            String key = HOLD_BOOKING_PREFIX + data.bookingId();
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize hold data", e);
        }
    }

    public BookingHoldData getHoldData(String bookingId) {
        try {
            String key = HOLD_BOOKING_PREFIX + bookingId;
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, BookingHoldData.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize hold data", e);
        }
    }

    public void removeHoldData(String bookingId) {
        redisTemplate.delete(HOLD_BOOKING_PREFIX + bookingId);
    }

    public boolean hasBookedSeatsCache(String showId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BOOKED_PREFIX + showId));
    }

    public Set<String> getBookedSeats(String showId) {
        Set<String> members = redisTemplate.opsForSet().members(BOOKED_PREFIX + showId);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>(members);
        result.remove(EMPTY_MARKER);
        return result;
    }

    public void setBookedSeats(String showId, Collection<String> seatNumbers) {
        String key = BOOKED_PREFIX + showId;
        redisTemplate.delete(key);
        if (seatNumbers == null || seatNumbers.isEmpty()) {
            redisTemplate.opsForSet().add(key, EMPTY_MARKER);
        } else {
            redisTemplate.opsForSet().add(key, seatNumbers.toArray(new String[0]));
        }
        redisTemplate.expire(key, BOOKED_CACHE_TTL);
    }

    public void addBookedSeats(String showId, Collection<String> seatNumbers) {
        if (seatNumbers == null || seatNumbers.isEmpty()) {
            return;
        }
        String key = BOOKED_PREFIX + showId;
        redisTemplate.opsForSet().remove(key, EMPTY_MARKER);
        redisTemplate.opsForSet().add(key, seatNumbers.toArray(new String[0]));
        redisTemplate.expire(key, BOOKED_CACHE_TTL);
    }

    public void removeBookedSeats(String showId, Collection<String> seatNumbers) {
        if (seatNumbers == null || seatNumbers.isEmpty()) {
            return;
        }
        String key = BOOKED_PREFIX + showId;
        redisTemplate.opsForSet().remove(key, (Object[]) seatNumbers.toArray(new String[0]));
        Long remaining = redisTemplate.opsForSet().size(key);
        if (remaining != null && remaining == 0) {
            redisTemplate.opsForSet().add(key, EMPTY_MARKER);
            redisTemplate.expire(key, BOOKED_CACHE_TTL);
        }
    }

    private String buildKey(String showId, String seatNumber) {
        return HOLD_PREFIX + showId + ":" + seatNumber;
    }
}
