package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemResponseDto {
    @NotNull
    private Long itemId;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotNull
    private Long ownerId;
}
