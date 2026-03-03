package ru.practicum.shareit.item;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto mapToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }

    public static Comment mapToCreateComment(CommentCreateDto commentCreateDto) {
        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}
