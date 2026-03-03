package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDTO createBooking(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody BookingCreateDTO bookingCreateDTO) {
        return bookingService.createBooking(userId, bookingCreateDTO);
    }

    @PatchMapping("/{bookingId}")
    public BookingDTO approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable Long bookingId, @RequestParam boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDTO getBookingById (@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDTO> getUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDTO> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }
}
