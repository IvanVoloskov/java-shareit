package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingShortDTO {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
