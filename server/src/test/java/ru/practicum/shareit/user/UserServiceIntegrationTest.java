package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserCrud() {
        UserDto created = userService.create(UserDto.builder().name("Ivan").email("ivan_u@mail.com").build());
        assertNotNull(created.getId());

        UserDto fetched = userService.getById(created.getId());
        assertEquals("Ivan", fetched.getName());

        UserDto updated = userService.update(created.getId(), UserDto.builder().name("Ivan Updated").build());
        assertEquals("Ivan Updated", updated.getName());

        List<UserDto> all = userService.getAll();
        assertNotNull(all);

        userService.delete(created.getId());
        assertThrows(NotFoundException.class, () -> userService.getById(created.getId()));
        assertThrows(NotFoundException.class, () -> userService.update(999L, updated));
    }
}