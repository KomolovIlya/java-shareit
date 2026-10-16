package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingStateTest {

    @Test
    void testBookingStateFrom() {
        assertEquals(BookingState.ALL, BookingState.from("ALL"));
        assertEquals(BookingState.CURRENT, BookingState.from("CURRENT"));
        assertEquals(BookingState.PAST, BookingState.from("PAST"));
        assertEquals(BookingState.FUTURE, BookingState.from("FUTURE"));
        assertEquals(BookingState.WAITING, BookingState.from("WAITING"));
        assertEquals(BookingState.REJECTED, BookingState.from("REJECTED"));
        assertEquals(BookingState.ALL, BookingState.from(null));
        assertEquals(BookingState.ALL, BookingState.from("   "));

        assertThrows(ValidationException.class, () -> BookingState.from("UNKNOWN"));
    }
}