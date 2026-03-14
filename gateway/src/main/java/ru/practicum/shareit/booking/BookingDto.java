package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    @NotNull(message = "Дата старта не должна быть пустой")
    private LocalDateTime start;
    @NotNull(message = "Дата завершения не должна быть пустой")
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private Status status;
}