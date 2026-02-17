package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class ItemRequest {
    int id;
    String text;
    User requestor;
    LocalDate created;
}

