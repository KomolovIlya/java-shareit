package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDtoSerialization() throws Exception {
        LocalDateTime created = LocalDateTime.of(2026, 11, 15, 14, 30, 0);

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(5L)
                .description("Ищу перфоратор")
                .created(created)
                .items(Collections.emptyList())
                .build();

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(requestDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(requestDto.getDescription());
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").startsWith(requestDto.getCreated().toString());
        assertThat(result).hasJsonPathArrayValue("$.items");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }
}