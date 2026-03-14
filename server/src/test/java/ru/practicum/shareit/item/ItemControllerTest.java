package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private CommentDto commentDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Drill");
        itemCreateDto.setDescription("Powerful drill");
        itemCreateDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great tool!");
        commentDto.setAuthorName("Booker");
        commentDto.setCreated(LocalDateTime.now());

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Great tool!");
    }

    @Test
    void addItem_ShouldReturnItem() throws Exception {
        when(itemService.addItem(eq(1L), any(ItemCreateDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void addItem_WithRequestId_ShouldReturnItem() throws Exception {
        when(itemService.addItem(eq(1L), any(ItemCreateDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("requestId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void addItem_WithoutUserId_ShouldReturnError() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Drill");

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setName("New Drill");
        updatedItem.setDescription("Powerful drill");
        updatedItem.setAvailable(true);

        when(itemService.updateItem(eq(1L), any(ItemDto.class))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Drill"));
    }

    @Test
    void updateItem_WithAllFields_ShouldReturnUpdatedItem() throws Exception {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Drill");
        updateDto.setDescription("New Description");
        updateDto.setAvailable(false);

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setName("New Drill");
        updatedItem.setDescription("New Description");
        updatedItem.setAvailable(false);

        when(itemService.updateItem(eq(1L), any(ItemDto.class))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Drill"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void updateItem_WithoutUserId_ShouldReturnError() throws Exception {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Drill");

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getItemById_ShouldReturnItem() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getItemById_WithInvalidId_ShouldReturnError() throws Exception {
        when(itemService.getItemById(999L)).thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(get("/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void userItems_ShouldReturnList() throws Exception {
        when(itemService.userItems(1L)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void userItems_WithoutUserId_ShouldReturnError() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void userItems_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        when(itemService.userItems(1L)).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getItemsByDescription_ShouldReturnList() throws Exception {
        when(itemService.getItemsByDescription("drill")).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void getItemsByDescription_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        when(itemService.getItemsByDescription("")).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getItemsByDescription_WithoutText_ShouldReturnError() throws Exception {
        mockMvc.perform(get("/items/search"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getItemsByDescription_WithNullText_ShouldReturnError() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", (String) null))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addComment_ShouldReturnComment() throws Exception {
        when(itemService.addComment(eq(2L), eq(1L), any(CommentCreateDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great tool!"))
                .andExpect(jsonPath("$.authorName").value("Booker"));
    }

    @Test
    void addComment_WithoutUserId_ShouldReturnError() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addComment_WithEmptyText_ShouldReturnError() throws Exception {
        CommentCreateDto emptyComment = new CommentCreateDto();
        emptyComment.setText("");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyComment)))
                .andExpect(status().isOk());
    }
}