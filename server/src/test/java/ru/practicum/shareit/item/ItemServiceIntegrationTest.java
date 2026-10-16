package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Test
    void testCreateUpdateSearchAndBookingsOnItem() {
        UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner_it@mail.com").build());
        UserDto booker = userService.create(UserDto.builder().name("Booker").email("booker_it@mail.com").build());
        UserDto stranger = userService.create(UserDto.builder().name("Stranger").email("stranger_it@mail.com").build());

        ItemDto created = itemService.create(owner.getId(), ItemDto.builder()
                .name("Шуруповерт")
                .description("Аккумуляторный")
                .available(true)
                .build());
        assertNotNull(created.getId());

        ItemDto updateDto = ItemDto.builder()
                .name("Шуруповерт Pro")
                .description("Аккумуляторный мощный")
                .available(false)
                .build();
        ItemDto updated = itemService.update(owner.getId(), created.getId(), updateDto);
        assertEquals("Шуруповерт Pro", updated.getName());
        assertEquals("Аккумуляторный мощный", updated.getDescription());
        assertFalse(updated.getAvailable());

        assertThrows(NotFoundException.class, () -> itemService.update(stranger.getId(), created.getId(), updateDto));
        assertThrows(NotFoundException.class, () -> itemService.update(999L, created.getId(), updateDto));
        assertThrows(NotFoundException.class, () -> itemService.update(owner.getId(), 999L, updateDto));

        itemService.update(owner.getId(), created.getId(), ItemDto.builder().available(true).build());

        LocalDateTime now = LocalDateTime.now();
        BookingDto pastBooking = bookingService.create(booker.getId(), BookingInputDto.builder()
                .itemId(created.getId())
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build());
        bookingService.approve(owner.getId(), pastBooking.getId(), true);

        BookingDto futureBooking = bookingService.create(booker.getId(), BookingInputDto.builder()
                .itemId(created.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());
        bookingService.approve(owner.getId(), futureBooking.getId(), true);

        ItemDto ownerView = itemService.getById(created.getId(), owner.getId());
        assertNotNull(ownerView.getLastBooking());
        assertNotNull(ownerView.getNextBooking());

        ItemDto strangerView = itemService.getById(created.getId(), stranger.getId());
        assertNull(strangerView.getLastBooking());
        assertNull(strangerView.getNextBooking());

        List<ItemDto> ownerItems = itemService.getByOwnerId(owner.getId());
        assertEquals(1, ownerItems.size());
        assertEquals(created.getId(), ownerItems.get(0).getId());

        List<ItemDto> emptyOwnerItems = itemService.getByOwnerId(stranger.getId());
        assertTrue(emptyOwnerItems.isEmpty());

        List<ItemDto> searchResults = itemService.search("мощный");
        assertEquals(1, searchResults.size());
        assertTrue(itemService.search("").isEmpty());
        assertTrue(itemService.search(null).isEmpty());

        CommentDto comment = itemService.addComment(booker.getId(), created.getId(),
                CommentDto.builder().text("Отличный шуруповерт!").build());
        assertNotNull(comment.getId());
        assertEquals("Booker", comment.getAuthorName());

        assertThrows(ValidationException.class, () -> itemService.addComment(stranger.getId(), created.getId(),
                CommentDto.builder().text("Не брал вещь").build()));
    }
}