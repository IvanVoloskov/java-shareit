package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class Booking {
    int id;
    LocalDate start;
    LocalDate end;
    Item item;
    User booker;
    Status status;
}
