package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {
    public static BookingDTO mapToDto(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setStart(booking.getStart());
        bookingDTO.setEnd(booking.getEnd());
        bookingDTO.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDTO.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDTO.setStatus(booking.getStatus());
        return bookingDTO;
    }

    public static Booking mapToCreate(BookingCreateDTO bookingCreateDTO) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateDTO.getStart());
        booking.setEnd(bookingCreateDTO.getEnd());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingShortDTO mapToShortDto(Booking booking) {
        BookingShortDTO bookingShortDTO = new BookingShortDTO();
        bookingShortDTO.setId(booking.getId());
        bookingShortDTO.setBookerId(booking.getBooker().getId());
        bookingShortDTO.setStart(booking.getStart());
        bookingShortDTO.setEnd(booking.getEnd());
        return bookingShortDTO;
    }
}
