package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void mapToDto_ShouldMapAllFields() {
        User author = new User();
        author.setId(1L);
        author.setName("Author");
        author.setEmail("author@mail.com");

        Item item = new Item();
        item.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great tool!");
        comment.setCreated(LocalDateTime.of(2026, 3, 14, 12, 0, 0));
        comment.setItem(item);
        comment.setAuthor(author);

        CommentDto dto = CommentMapper.mapToDto(comment);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Great tool!", dto.getText());
        assertEquals("Author", dto.getAuthorName());
        assertEquals(LocalDateTime.of(2026, 3, 14, 12, 0, 0), dto.getCreated());
    }

    @Test
    void mapToCreateComment_ShouldMapTextAndSetCreated() {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Great tool!");

        Comment comment = CommentMapper.mapToCreateComment(createDto);

        assertNotNull(comment);
        assertEquals("Great tool!", comment.getText());
        assertNotNull(comment.getCreated());
        assertNull(comment.getId());
        assertNull(comment.getItem());
        assertNull(comment.getAuthor());
    }

    @Test
    void mapToCreateComment_WithNullText_ShouldCreateCommentWithNullText() {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText(null);

        Comment comment = CommentMapper.mapToCreateComment(createDto);

        assertNotNull(comment);
        assertNull(comment.getText());
        assertNotNull(comment.getCreated());
    }

    @Test
    void mapToCreateComment_ShouldSetCreated() {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Text");

        Comment comment = CommentMapper.mapToCreateComment(createDto);

        assertEquals("Text", comment.getText());
        assertNotNull(comment.getCreated());
    }
}