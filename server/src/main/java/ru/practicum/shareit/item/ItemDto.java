package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.booking.BookingShortDto;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
}