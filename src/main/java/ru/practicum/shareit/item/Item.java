package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    private Long id;
    private User owner;
    private String name;
    private String description;
    private boolean isAvailable;
}