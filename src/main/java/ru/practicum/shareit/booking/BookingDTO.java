package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long id;
    @NotBlank(message = "Дата старта не должна быть пустой")
    @NotNull
    private LocalDateTime start;
    @NotBlank(message = "Дата завершения не должна быть пустой")
    @NotNull
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private Status status;
}