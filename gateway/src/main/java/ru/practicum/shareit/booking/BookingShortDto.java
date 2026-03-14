package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingShortDto {
    private Long id;
    @NotNull
    private Long bookerId;
    @NotNull(message = "Дата старта не должна быть пустой")
    private LocalDateTime start;
    @NotNull(message = "Дата завершения не должна быть пустой")
    private LocalDateTime end;
}
