package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + stateParam));

        log.info("Gateway: получение бронирований пользователя {} со статусом {}", userId, state);
        return bookingClient.getUserBookings(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody BookingCreateDto createDto) {
        log.info("Gateway: создание бронирования пользователем {}", userId);

        if (createDto.getStart().isAfter(createDto.getEnd())) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты окончания");
        }
        if (createDto.getStart().equals(createDto.getEnd())) {
            throw new IllegalArgumentException("Даты начала и окончания не могут совпадать");
        }

        return bookingClient.createBooking(userId, createDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Gateway: получение бронирования {} пользователем {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("Gateway: {} бронирования {} владельцем {}",
                approved ? "подтверждение" : "отклонение", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + stateParam));

        log.info("Gateway: получение бронирований для вещей владельца {} со статусом {}", ownerId, state);
        return bookingClient.getOwnerBookings(ownerId, state);
    }
}