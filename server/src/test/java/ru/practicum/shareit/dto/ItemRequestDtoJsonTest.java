package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testSerialize() throws IOException {
        ItemResponseDto response = new ItemResponseDto();
        response.setItemId(1L);
        response.setName("Drill");
        response.setOwnerId(2L);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a drill");
        requestDto.setCreated(LocalDateTime.of(2026, 3, 14, 12, 0, 0));
        requestDto.setItems(List.of(response));

        assertThat(json.write(requestDto))
                .hasJsonPathNumberValue("$.id")
                .hasJsonPathStringValue("$.description")
                .hasJsonPathStringValue("$.created")
                .hasJsonPathArrayValue("$.items");

        assertThat(json.write(requestDto)).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(json.write(requestDto)).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a drill");
        assertThat(json.write(requestDto)).extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-03-14T12:00:00");
        assertThat(json.write(requestDto)).extractingJsonPathNumberValue("$.items[0].itemId")
                .isEqualTo(1);
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2026-03-14T12:00:00\",\"items\":[{\"itemId\":1,\"name\":\"Drill\",\"ownerId\":2}]}";

        ItemRequestDto requestDto = json.parseObject(content);

        assertThat(requestDto.getId()).isEqualTo(1L);
        assertThat(requestDto.getDescription()).isEqualTo("Need a drill");
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2026, 3, 14, 12, 0, 0));
        assertThat(requestDto.getItems()).hasSize(1);
        assertThat(requestDto.getItems().get(0).getItemId()).isEqualTo(1L);
        assertThat(requestDto.getItems().get(0).getName()).isEqualTo("Drill");
        assertThat(requestDto.getItems().get(0).getOwnerId()).isEqualTo(2L);
    }

    @Test
    void testDeserializeWithEmptyItems() throws IOException {
        String content = "{\"id\":2,\"description\":\"Need a hammer\",\"created\":\"2026-03-14T13:00:00\",\"items\":[]}";

        ItemRequestDto requestDto = json.parseObject(content);

        assertThat(requestDto.getId()).isEqualTo(2L);
        assertThat(requestDto.getDescription()).isEqualTo("Need a hammer");
        assertThat(requestDto.getItems()).isEmpty();
    }
}