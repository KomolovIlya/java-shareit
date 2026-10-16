package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequestTest() throws Exception {
        ItemRequestDto inputDto = ItemRequestDto.builder()
                .description("Нужна лестница")
                .build();

        ItemRequestDto outputDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна лестница")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.create(eq(1L), any(ItemRequestDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Нужна лестница")));
    }

    @Test
    void getUserRequestsTest() throws Exception {
        ItemRequestDto outputDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна лестница")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.getUserRequests(1L)).thenReturn(List.of(outputDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("Нужна лестница")));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        ItemRequestDto outputDto = ItemRequestDto.builder()
                .id(2L)
                .description("Нужен шуруповерт")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.getAllRequests(1L)).thenReturn(List.of(outputDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        ItemRequestDto outputDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна лестница")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.getById(1L, 1L)).thenReturn(outputDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Нужна лестница")));
    }
}