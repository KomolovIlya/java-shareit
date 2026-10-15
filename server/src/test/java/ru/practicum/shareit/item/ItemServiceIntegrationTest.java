package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void testCreateUpdateAndSearchItem() {
        UserDto user = userService.create(UserDto.builder().name("ItemOwner").email("item_owner@mail.com").build());
        UserDto stranger = userService.create(UserDto.builder().name("Stranger").email("stranger@mail.com").build());

        ItemDto itemDto = ItemDto.builder()
                .name("Шуруповерт")
                .description("Аккумуляторный")
                .available(true)
                .build();
        ItemDto created = itemService.create(user.getId(), itemDto);
        assertNotNull(created.getId());

        ItemDto updateDto = ItemDto.builder()
                .name("Шуруповерт Pro")
                .description("Аккумуляторный мощный")
                .available(false)
                .build();
        ItemDto updated = itemService.update(user.getId(), created.getId(), updateDto);
        assertEquals("Шуруповерт Pro", updated.getName());
        assertEquals("Аккумуляторный мощный", updated.getDescription());
        assertFalse(updated.getAvailable());

        assertThrows(NotFoundException.class, () -> itemService.update(stranger.getId(), created.getId(), updateDto));

        itemService.update(user.getId(), created.getId(), ItemDto.builder().available(true).build());
        List<ItemDto> searchResults = itemService.search("мощный");
        assertEquals(1, searchResults.size());

        List<ItemDto> emptySearch = itemService.search("");
        assertTrue(emptySearch.isEmpty());
        List<ItemDto> nullSearch = itemService.search(null);
        assertTrue(nullSearch.isEmpty());

        ItemDto fetched = itemService.getById(created.getId(), stranger.getId());
        assertEquals("Шуруповерт Pro", fetched.getName());

        CommentDto commentDto = CommentDto.builder().text("Отзыв без аренды").build();
        assertThrows(ValidationException.class, () -> itemService.addComment(stranger.getId(), created.getId(), commentDto));
    }
}