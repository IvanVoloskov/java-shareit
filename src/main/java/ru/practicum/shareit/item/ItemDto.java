package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.booking.BookingShortDTO;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDTO lastBooking;
    private BookingShortDTO nextBooking;
    private List<CommentDto> comments;
}