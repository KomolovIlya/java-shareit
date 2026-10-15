package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void createAndApproveBookingTest() {
        UserDto owner = userService.create(UserDto.builder()
                .name("Владелец")
                .email("owner@booking.com")
                .build());

        UserDto booker = userService.create(UserDto.builder()
                .name("Арендатор")
                .email("booker@booking.com")
                .build());

        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Стремянка")
                .description("Высокая стремянка")
                .available(true)
                .build());

        LocalDateTime now = LocalDateTime.now();
        BookingInputDto inputDto = BookingInputDto.builder()
                .itemId(item.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        BookingDto created = bookingService.create(booker.getId(), inputDto);
        assertNotNull(created.getId());
        assertEquals(BookingStatus.WAITING, created.getStatus());

        BookingDto approved = bookingService.approve(owner.getId(), created.getId(), true);
        assertEquals(BookingStatus.APPROVED, approved.getStatus());

        List<BookingDto> bookerBookings = bookingService.getAllByBooker(booker.getId(), "ALL");
        assertEquals(1, bookerBookings.size());

        List<BookingDto> ownerBookings = bookingService.getAllByOwner(owner.getId(), "ALL");
        assertEquals(1, ownerBookings.size());
    }
}