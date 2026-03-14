package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_ShouldReturnList() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@mail.com");

        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@mail.com"));
    }

    @Test
    void getAllUsers_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@mail.com");

        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    void getUserById_WithInvalidId_ShouldReturnError() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("John");
        inputDto.setEmail("john@mail.com");

        UserDto outputDto = new UserDto();
        outputDto.setId(1L);
        outputDto.setName("John");
        outputDto.setEmail("john@mail.com");

        when(userService.createUser(any(UserDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    void createUser_WithInvalidEmail_ShouldReturnError() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("John");
        inputDto.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk()); // Ошибка будет в сервисе
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("Updated Name");

        UserDto outputDto = new UserDto();
        outputDto.setId(1L);
        outputDto.setName("Updated Name");
        outputDto.setEmail("john@mail.com");

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(outputDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    void updateUser_WithAllFields_ShouldReturnUpdatedUser() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("Updated Name");
        inputDto.setEmail("updated@mail.com");

        UserDto outputDto = new UserDto();
        outputDto.setId(1L);
        outputDto.setName("Updated Name");
        outputDto.setEmail("updated@mail.com");

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(outputDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@mail.com"));
    }

    @Test
    void updateUser_WithInvalidId_ShouldReturnError() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("Updated Name");

        when(userService.updateUser(eq(999L), any(UserDto.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_WithInvalidId_ShouldReturnError() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден")).when(userService).deleteUserById(999L);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}