package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDto {

    @Email(message = "Некорректный формат email")
    private String email;

    private String name;
}