package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
    @NotNull(message = "Дата не может быть пустой")
    private LocalDateTime created;
    private List<ItemResponseDto> items;
}
