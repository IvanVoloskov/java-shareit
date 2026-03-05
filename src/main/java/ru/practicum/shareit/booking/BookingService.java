package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDTO createBooking(Long userId, BookingCreateDTO bookingCreateDTO);

    BookingDTO approveBooking(Long userId, Long bookingId, boolean approved);

    BookingDTO getBookingById(Long userId, Long bookingId);

    List<BookingDTO> getUserBookings(Long userId, String state);

    List<BookingDTO> getOwnerBookings(Long ownerId, String state);
}
