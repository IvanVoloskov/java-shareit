package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemCreateRequestDto {
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
}
