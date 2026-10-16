package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDtoSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 12, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 2, 10, 0, 0);

        UserDto booker = UserDto.builder()
                .id(1L)
                .name("Booker")
                .email("booker@test.com")
                .build();

        ItemDto item = ItemDto.builder()
                .id(2L)
                .name("Дрель")
                .description("Мощная")
                .available(true)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(10L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());

        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").startsWith(bookingDto.getStart().toString());

        assertThat(result).hasJsonPathStringValue("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end").startsWith(bookingDto.getEnd().toString());

        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().name());

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookingDto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookingDto.getBooker().getName());

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingDto.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(bookingDto.getItem().getName());
    }
}