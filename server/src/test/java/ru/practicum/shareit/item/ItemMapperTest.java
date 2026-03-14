package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto_ShouldMapAllFields() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Drill", dto.getName());
        assertEquals("Powerful drill", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertNull(dto.getRequestId());
    }

    @Test
    void toItemDto_WithRequest_ShouldSetRequestId() {
        User owner = new User();
        owner.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(5L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertNotNull(dto);
        assertNull(dto.getRequestId());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");
        dto.setDescription("Powerful drill");
        dto.setAvailable(true);

        Item item = ItemMapper.toEntity(dto);

        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Drill", item.getName());
        assertEquals("Powerful drill", item.getDescription());
        assertTrue(item.isAvailable());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
    }

    @Test
    void toEntity_WithNullAvailable_ShouldSetDefaultFalse() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");
        dto.setDescription("Powerful drill");
        dto.setAvailable(null);

        Item item = ItemMapper.toEntity(dto);

        assertNotNull(item);
        assertFalse(item.isAvailable());
    }

    @Test
    void toCreateDto_ShouldMapAllFields() {
        User owner = new User();
        owner.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(5L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        ItemCreateDto createDto = ItemMapper.toCreateDto(item);

        assertNotNull(createDto);
        assertEquals("Drill", createDto.getName());
        assertEquals("Powerful drill", createDto.getDescription());
        assertTrue(createDto.getAvailable());
        assertEquals(5L, createDto.getRequestId());
    }
}