package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void mapToDto_ShouldMapAllFields() {
        User booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2026, 3, 15, 10, 0, 0));
        booking.setEnd(LocalDateTime.of(2026, 3, 16, 18, 0, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        BookingDto dto = BookingMapper.mapToDto(booking);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(LocalDateTime.of(2026, 3, 15, 10, 0, 0), dto.getStart());
        assertEquals(LocalDateTime.of(2026, 3, 16, 18, 0, 0), dto.getEnd());
        assertNotNull(dto.getItem());
        assertEquals(1L, dto.getItem().getId());
        assertEquals("Drill", dto.getItem().getName());
        assertNotNull(dto.getBooker());
        assertEquals(2L, dto.getBooker().getId());
        assertEquals("Booker", dto.getBooker().getName());
        assertEquals(Status.WAITING, dto.getStatus());
    }

    @Test
    void mapToCreate_ShouldMapItemIdAndDatesAndSetStatusWaiting() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 15, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 16, 18, 0, 0);

        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        createDto.setStart(start);
        createDto.setEnd(end);

        Booking booking = BookingMapper.mapToCreate(createDto);

        assertNotNull(booking);
        assertNull(booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(Status.WAITING, booking.getStatus());
        assertNull(booking.getItem());
        assertNull(booking.getBooker());
    }

    @Test
    void mapToShortDto_ShouldMapIdBookerIdAndDates() {
        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2026, 3, 15, 10, 0, 0));
        booking.setEnd(LocalDateTime.of(2026, 3, 16, 18, 0, 0));
        booking.setBooker(booker);

        BookingShortDto shortDto = BookingMapper.mapToShortDto(booking);

        assertNotNull(shortDto);
        assertEquals(1L, shortDto.getId());
        assertEquals(2L, shortDto.getBookerId());
        assertEquals(LocalDateTime.of(2026, 3, 15, 10, 0, 0), shortDto.getStart());
        assertEquals(LocalDateTime.of(2026, 3, 16, 18, 0, 0), shortDto.getEnd());
    }
}