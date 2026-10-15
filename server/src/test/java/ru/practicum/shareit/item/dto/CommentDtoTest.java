package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDtoSerialization() throws Exception {
        LocalDateTime created = LocalDateTime.of(2026, 10, 20, 18, 45, 0);

        CommentDto commentDto = CommentDto.builder()
                .id(7L)
                .text("Отличный инструмент, всё сработало!")
                .authorName("Алексей")
                .created(created)
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(7);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличный инструмент, всё сработало!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Алексей");
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").startsWith("2026-10-20T18:45:00");
    }
}