package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                             @RequestBody BookingInputDto bookingInputDto) {
        log.info("POST /bookings, userId: {}, body: {}", userId, bookingInputDto);
        return bookingService.create(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam Boolean approved) {
        log.info("PATCH /bookings/{}, userId: {}, approved: {}", bookingId, userId, approved);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PathVariable Long bookingId) {
        log.info("GET /bookings/{}, userId: {}", bookingId, userId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings?state={}, userId: {}", state, userId);
        return bookingService.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner?state={}, userId: {}", state, userId);
        return bookingService.getAllByOwner(userId, state);
    }
}