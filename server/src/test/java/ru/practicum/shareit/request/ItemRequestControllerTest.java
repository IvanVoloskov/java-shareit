package ru.practicum.shareit.request;

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

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    private ItemRequestDto requestDto;
    private ItemCreateRequestDto createDto;
    private ItemResponseDto responseDto;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        responseDto = new ItemResponseDto();
        responseDto.setItemId(1L);
        responseDto.setName("Drill");
        responseDto.setOwnerId(3L);

        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a drill");
        requestDto.setCreated(LocalDateTime.now());
        requestDto.setItems(List.of(responseDto));

        createDto = new ItemCreateRequestDto();
        createDto.setDescription("Need a drill");
    }

    @Test
    void createRequest_ShouldReturnCreatedRequest() throws Exception {
        when(requestService.createRequest(eq(1L), any(ItemCreateRequestDto.class))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.items[0].itemId").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Drill"));
    }

    @Test
    void createRequest_WithoutUserId_ShouldReturnError() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createRequest_WithEmptyDescription_ShouldReturnError() throws Exception {
        ItemCreateRequestDto emptyDto = new ItemCreateRequestDto();
        emptyDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserRequests_ShouldReturnList() throws Exception {
        when(requestService.getUserRequest(1L)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"))
                .andExpect(jsonPath("$[0].items[0].itemId").value(1));
    }

    @Test
    void getUserRequests_WithoutUserId_ShouldReturnError() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUserRequests_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        when(requestService.getUserRequest(1L)).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllRequests_ShouldReturnList() throws Exception {
        when(requestService.getAllRequests(1L)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));
    }

    @Test
    void getAllRequests_WithoutUserId_ShouldReturnError() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllRequests_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        when(requestService.getAllRequests(1L)).thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getRequestById_ShouldReturnRequest() throws Exception {
        when(requestService.getRequestById(1L, 1L)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.items[0].itemId").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Drill"));
    }

    @Test
    void getRequestById_WithoutUserId_ShouldReturnError() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getRequestById_WithInvalidId_ShouldReturnError() throws Exception {
        when(requestService.getRequestById(1L, 999L)).thenThrow(new NotFoundException("Запрос не найден"));

        mockMvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}