package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUserTest() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Пользователь 1")
                .email("user1@mail.com")
                .build();

        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Пользователь 1")))
                .andExpect(jsonPath("$.email", is("user1@mail.com")));
    }

    @Test
    void updateUserTest() throws Exception {
        UserDto updateDto = UserDto.builder()
                .name("Обновленный")
                .email("updated@mail.com")
                .build();

        UserDto resultDto = UserDto.builder()
                .id(1L)
                .name("Обновленный")
                .email("updated@mail.com")
                .build();

        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(resultDto);

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Обновленный")))
                .andExpect(jsonPath("$.email", is("updated@mail.com")));
    }

    @Test
    void getUserByIdTest() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Пользователь 1")
                .email("user1@mail.com")
                .build();

        when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Пользователь 1")));
    }

    @Test
    void getAllUsersTest() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Пользователь 1")
                .email("user1@mail.com")
                .build();

        when(userService.getAll()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Пользователь 1")));
    }

    @Test
    void deleteUserTest() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}