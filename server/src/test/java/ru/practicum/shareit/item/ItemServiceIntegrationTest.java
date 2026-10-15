package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void createAndGetItemTest() {
        UserDto owner = userService.create(UserDto.builder()
                .name("Владелец")
                .email("owner@item.com")
                .build());

        ItemDto itemDto = ItemDto.builder()
                .name("Перфоратор")
                .description("Мощный перфоратор")
                .available(true)
                .build();

        ItemDto created = itemService.create(owner.getId(), itemDto);
        assertNotNull(created.getId());
        assertEquals("Перфоратор", created.getName());

        ItemDto found = itemService.getById(created.getId(), owner.getId());
        assertEquals("Перфоратор", found.getName());
        assertTrue(found.getAvailable());
    }

    @Test
    void getByOwnerIdTest() {
        UserDto owner = userService.create(UserDto.builder()
                .name("Владелец 2")
                .email("owner2@item.com")
                .build());

        ItemDto item1 = ItemDto.builder().name("Пила").description("Дисковая").available(true).build();
        ItemDto item2 = ItemDto.builder().name("Лобзик").description("Электрический").available(true).build();

        itemService.create(owner.getId(), item1);
        itemService.create(owner.getId(), item2);

        List<ItemDto> items = itemService.getByOwnerId(owner.getId());
        assertEquals(2, items.size());
    }

    @Test
    void searchItemsTest() {
        UserDto owner = userService.create(UserDto.builder()
                .name("Владелец 3")
                .email("owner3@item.com")
                .build());

        ItemDto item1 = ItemDto.builder().name("Шуруповерт Bosch").description("Аккумуляторный").available(true).build();
        ItemDto item2 = ItemDto.builder().name("Шуруповерт Makita").description("Сетевой").available(false).build();

        itemService.create(owner.getId(), item1);
        itemService.create(owner.getId(), item2);

        List<ItemDto> found = itemService.search("шуруповерт");
        assertEquals(1, found.size());
        assertEquals("Шуруповерт Bosch", found.get(0).getName());

        List<ItemDto> empty = itemService.search("");
        assertTrue(empty.isEmpty());
    }
}