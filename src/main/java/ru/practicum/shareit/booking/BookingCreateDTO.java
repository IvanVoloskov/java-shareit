package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDTO {
    @NotNull(message = "ID вещи обязателен")
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
