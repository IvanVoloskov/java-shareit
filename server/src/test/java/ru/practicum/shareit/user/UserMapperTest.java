package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDto_ShouldMapAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@mail.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@mail.com", dto.getEmail());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("john@mail.com");

        User user = UserMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@mail.com", user.getEmail());
    }

    @Test
    void toEntity_WithNullId_ShouldMapNullId() {
        UserDto dto = new UserDto();
        dto.setId(null);
        dto.setName("John Doe");
        dto.setEmail("john@mail.com");

        User user = UserMapper.toEntity(dto);

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@mail.com", user.getEmail());
    }
}