package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    @NotNull(message = "ID вещи обязателен")
    private Long itemId;
    @NotNull(message = "Дата старта не должна быть пустой")
    private LocalDateTime start;
    @NotNull(message = "Дата завершения не должна быть пустой")
    private LocalDateTime end;
    private Status status;
}
