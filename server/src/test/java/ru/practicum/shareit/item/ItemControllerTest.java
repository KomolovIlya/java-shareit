package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItemTest() throws Exception {
        ItemDto inputDto = ItemDto.builder()
                .name("Дрель")
                .description("Мощная ударная дрель")
                .available(true)
                .build();

        ItemDto outputDto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Мощная ударная дрель")
                .available(true)
                .build();

        when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Дрель")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto updateDto = ItemDto.builder()
                .name("Дрель+")
                .build();

        ItemDto outputDto = ItemDto.builder()
                .id(1L)
                .name("Дрель+")
                .description("Мощная ударная дрель")
                .available(true)
                .build();

        when(itemService.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(outputDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Дрель+")));
    }

    @Test
    void getItemByIdTest() throws Exception {
        ItemDto outputDto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Мощная ударная дрель")
                .available(true)
                .comments(Collections.emptyList())
                .build();

        when(itemService.getById(eq(1L), eq(1L))).thenReturn(outputDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Дрель")));
    }

    @Test
    void getItemsByOwnerTest() throws Exception {
        ItemDto outputDto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Мощная ударная дрель")
                .available(true)
                .build();

        when(itemService.getByOwnerId(1L)).thenReturn(List.of(outputDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Дрель")));
    }

    @Test
    void searchItemsTest() throws Exception {
        ItemDto outputDto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Мощная ударная дрель")
                .available(true)
                .build();

        when(itemService.search("дрель")).thenReturn(List.of(outputDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Дрель")));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentDto inputComment = CommentDto.builder()
                .text("Отличный инструмент!")
                .build();

        CommentDto outputComment = CommentDto.builder()
                .id(1L)
                .text("Отличный инструмент!")
                .authorName("Иван")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(outputComment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(inputComment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Отличный инструмент!")))
                .andExpect(jsonPath("$.authorName", is("Иван")));
    }
}