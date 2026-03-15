package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {
    public static BookingDto mapToDto(Booking booking) {
        BookingDto bookingDTO = new BookingDto();
        bookingDTO.setId(booking.getId());
        bookingDTO.setStart(booking.getStart());
        bookingDTO.setEnd(booking.getEnd());
        bookingDTO.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDTO.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDTO.setStatus(booking.getStatus());
        return bookingDTO;
    }

    public static Booking mapToCreate(BookingCreateDto bookingCreateDTO) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateDTO.getStart());
        booking.setEnd(bookingCreateDTO.getEnd());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingShortDto mapToShortDto(Booking booking) {
        BookingShortDto bookingShortDTO = new BookingShortDto();
        bookingShortDTO.setId(booking.getId());
        bookingShortDTO.setBookerId(booking.getBooker().getId());
        bookingShortDTO.setStart(booking.getStart());
        bookingShortDTO.setEnd(booking.getEnd());
        return bookingShortDTO;
    }
}
