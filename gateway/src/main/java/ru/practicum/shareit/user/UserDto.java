package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    Long id;
    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email не должен быть пустым")
    String email;
    @NotBlank(message = "Имя не должно быть пустым")
    String name;
}
