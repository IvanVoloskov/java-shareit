package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testBookingDto() throws IOException {
        UserDto booker = new UserDto();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");

        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2026, 3, 14, 10, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2026, 3, 15, 18, 0, 0));
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(Status.WAITING);

        assertThat(json.write(bookingDto))
                .hasJsonPathNumberValue("$.id")
                .hasJsonPathStringValue("$.start")
                .hasJsonPathStringValue("$.end")
                .hasJsonPathMapValue("$.item")
                .hasJsonPathMapValue("$.booker")
                .hasJsonPathStringValue("$.status");

        assertThat(json.write(bookingDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-03-14T10:00:00");
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-03-15T18:00:00");
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.status")
                .isEqualTo("WAITING");
    }
}
