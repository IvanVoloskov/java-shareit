package ru.practicum.shareit.booking;

public enum Status {
    WAITING, // Ждёт подтверждения
    APPROVED, // Владелец подтвердил бронирование
    REJECTED, // Владелец отклонил бронирование
    CANCELED // Бронирование отклонено
}