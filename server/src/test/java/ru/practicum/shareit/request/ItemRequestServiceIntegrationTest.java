package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void createAndGetRequestsTest() {
        UserDto requester = userService.create(UserDto.builder()
                .name("Проситель")
                .email("req@integration.com")
                .build());

        UserDto otherUser = userService.create(UserDto.builder()
                .name("Другой")
                .email("other@integration.com")
                .build());

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Ищу сварочный аппарат")
                .build();

        ItemRequestDto created = itemRequestService.create(requester.getId(), requestDto);
        assertNotNull(created.getId());
        assertEquals("Ищу сварочный аппарат", created.getDescription());

        // otherUser добавляет вещь в ответ на этот запрос
        itemService.create(otherUser.getId(), ItemDto.builder()
                .name("Сварочный аппарат Ресанта")
                .description("Инверторный")
                .available(true)
                .requestId(created.getId())
                .build());

        List<ItemRequestDto> userRequests = itemRequestService.getUserRequests(requester.getId());
        assertEquals(1, userRequests.size());
        assertFalse(userRequests.get(0).getItems().isEmpty());
        assertEquals("Сварочный аппарат Ресанта", userRequests.get(0).getItems().get(0).getName());

        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests(otherUser.getId());
        assertEquals(1, allRequests.size());

        ItemRequestDto byId = itemRequestService.getById(requester.getId(), created.getId());
        assertEquals(created.getId(), byId.getId());
        assertEquals(1, byId.getItems().size());
    }
}