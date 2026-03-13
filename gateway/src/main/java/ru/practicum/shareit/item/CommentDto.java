package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст не может быть пустым")
    private String text;
    @NotNull(message = "Дата создания не должна быть пустой")
    private LocalDateTime created;
    private String authorName;
}
