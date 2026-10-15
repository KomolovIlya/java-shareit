package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.ItemOwnerBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testBookingLifecycleAndStates() {
        UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner_b@mail.com").build());
        UserDto booker = userService.create(UserDto.builder().name("Booker").email("booker_b@mail.com").build());

        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Дрель")
                .description("Мощная дрель")
                .available(true)
                .build());

        LocalDateTime now = LocalDateTime.now();
        BookingInputDto input = BookingInputDto.builder()
                .itemId(item.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        BookingDto booking = bookingService.create(booker.getId(), input);
        assertNotNull(booking.getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());

        BookingDto approved = bookingService.approve(owner.getId(), booking.getId(), true);
        assertEquals(BookingStatus.APPROVED, approved.getStatus());

        BookingDto fetched = bookingService.getById(booker.getId(), booking.getId());
        assertEquals(approved.getId(), fetched.getId());

        List<BookingDto> allByBooker = bookingService.getAllByBooker(booker.getId(), "ALL");
        assertEquals(1, allByBooker.size());

        List<BookingDto> futureByBooker = bookingService.getAllByBooker(booker.getId(), "FUTURE");
        assertEquals(1, futureByBooker.size());

        List<BookingDto> currentByBooker = bookingService.getAllByBooker(booker.getId(), "CURRENT");
        assertEquals(0, currentByBooker.size());

        List<BookingDto> pastByBooker = bookingService.getAllByBooker(booker.getId(), "PAST");
        assertEquals(0, pastByBooker.size());

        List<BookingDto> waitingByBooker = bookingService.getAllByBooker(booker.getId(), "WAITING");
        assertEquals(0, waitingByBooker.size());

        List<BookingDto> rejectedByBooker = bookingService.getAllByBooker(booker.getId(), "REJECTED");
        assertEquals(0, rejectedByBooker.size());

        List<BookingDto> allByOwner = bookingService.getAllByOwner(owner.getId(), "ALL");
        assertEquals(1, allByOwner.size());

        List<BookingDto> futureByOwner = bookingService.getAllByOwner(owner.getId(), "FUTURE");
        assertEquals(1, futureByOwner.size());

        List<BookingDto> currentByOwner = bookingService.getAllByOwner(owner.getId(), "CURRENT");
        assertEquals(0, currentByOwner.size());

        List<BookingDto> pastByOwner = bookingService.getAllByOwner(owner.getId(), "PAST");
        assertEquals(0, pastByOwner.size());

        List<BookingDto> waitingByOwner = bookingService.getAllByOwner(owner.getId(), "WAITING");
        assertEquals(0, waitingByOwner.size());

        List<BookingDto> rejectedByOwner = bookingService.getAllByOwner(owner.getId(), "REJECTED");
        assertEquals(0, rejectedByOwner.size());

        assertThrows(ItemOwnerBookingException.class, () -> bookingService.create(owner.getId(), input));

        assertThrows(NotFoundException.class, () -> bookingService.getById(999L, booking.getId()));

        assertThrows(ValidationException.class, () -> bookingService.approve(owner.getId(), booking.getId(), true));
    }
}