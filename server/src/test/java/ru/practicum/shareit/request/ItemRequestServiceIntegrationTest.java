package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testRequestLifecycle() {
        UserDto requestor = userService.create(UserDto.builder().name("ReqUser").email("requser@mail.com").build());
        UserDto responder = userService.create(UserDto.builder().name("RespUser").email("respuser@mail.com").build());

        ItemRequestDto request = itemRequestService.create(requestor.getId(),
                ItemRequestDto.builder().description("Нужна лестница").build());
        assertNotNull(request.getId());

        itemService.create(responder.getId(), ItemDto.builder()
                .name("Лестница стремянка")
                .description("3 метра")
                .available(true)
                .requestId(request.getId())
                .build());

        List<ItemRequestDto> ownRequests = itemRequestService.getUserRequests(requestor.getId());
        assertEquals(1, ownRequests.size());
        assertFalse(ownRequests.get(0).getItems().isEmpty());

        List<ItemRequestDto> otherRequests = itemRequestService.getAllRequests(responder.getId());
        assertEquals(1, otherRequests.size());

        ItemRequestDto fetched = itemRequestService.getById(requestor.getId(), request.getId());
        assertEquals(request.getId(), fetched.getId());
        assertEquals(1, fetched.getItems().size());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(requestor.getId(), 999L));
        assertThrows(NotFoundException.class, () -> itemRequestService.create(999L, request));
        assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(999L));
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(999L));
    }
}