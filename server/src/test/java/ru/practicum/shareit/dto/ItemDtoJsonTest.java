package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testItemDto() throws IOException {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Great tool!");
        comment.setAuthorName("User");
        comment.setCreated(LocalDateTime.of(2026, 3, 13, 12, 0, 0));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(5L);
        itemDto.setComments(List.of(comment));

        assertThat(json.write(itemDto))
                .hasJsonPathNumberValue("$.id")
                .hasJsonPathStringValue("$.name")
                .hasJsonPathStringValue("$.description")
                .hasJsonPathBooleanValue("$.available")
                .hasJsonPathNumberValue("$.requestId")
                .hasJsonPathArrayValue("$.comments");

        assertThat(json.write(itemDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json.write(itemDto)).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(json.write(itemDto)).extractingJsonPathStringValue("$.description").isEqualTo("Powerful drill");
        assertThat(json.write(itemDto)).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(json.write(itemDto)).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
    }
}