package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.ValidationException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String state) {
        if (state == null || state.isBlank()) {
            return ALL;
        }
        for (BookingState value : values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return value;
            }
        }
        throw new ValidationException("Unknown state: " + state);
    }
}