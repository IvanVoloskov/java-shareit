package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private Status status;
}