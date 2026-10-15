package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createAndGetUserTest() {
        UserDto userDto = UserDto.builder()
                .name("Ольга")
                .email("olga@integration.com")
                .build();

        UserDto created = userService.create(userDto);
        assertNotNull(created.getId());
        assertEquals("Ольга", created.getName());

        UserDto found = userService.getById(created.getId());
        assertEquals(created.getId(), found.getId());
        assertEquals("olga@integration.com", found.getEmail());
    }

    @Test
    void updateUserTest() {
        UserDto userDto = UserDto.builder()
                .name("Павел")
                .email("pavel@integration.com")
                .build();

        UserDto created = userService.create(userDto);

        UserDto updateDto = UserDto.builder()
                .name("Павел Обновленный")
                .build();

        UserDto updated = userService.update(created.getId(), updateDto);
        assertEquals("Павел Обновленный", updated.getName());
        assertEquals("pavel@integration.com", updated.getEmail());
    }

    @Test
    void getAllUsersTest() {
        UserDto user1 = UserDto.builder().name("Пользователь 1").email("u1@int.com").build();
        UserDto user2 = UserDto.builder().name("Пользователь 2").email("u2@int.com").build();

        userService.create(user1);
        userService.create(user2);

        List<UserDto> all = userService.getAll();
        assertEquals(2, all.size());
    }
}