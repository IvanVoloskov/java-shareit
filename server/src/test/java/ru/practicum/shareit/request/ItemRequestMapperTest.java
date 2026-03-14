package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void mapToEntity_ShouldMapAllFields() {
        User requestor = new User();
        requestor.setId(1L);

        ItemCreateRequestDto createDto = new ItemCreateRequestDto();
        createDto.setDescription("Need a drill");

        ItemRequest request = ItemRequestMapper.mapToEntity(createDto, requestor);

        assertNotNull(request);
        assertNull(request.getId());
        assertEquals("Need a drill", request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertNotNull(request.getCreated());
    }

    @Test
    void mapToDto_ShouldMapAllFields() {
        User requestor = new User();
        requestor.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.of(2026, 3, 14, 12, 0, 0));

        ItemRequestDto dto = ItemRequestMapper.mapToDto(request);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(LocalDateTime.of(2026, 3, 14, 12, 0, 0), dto.getCreated());
        assertNull(dto.getItems());
    }

    @Test
    void mapToItemResponseDto_ShouldMapAllFields() {
        User owner = new User();
        owner.setId(3L);

        Item item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setOwner(owner);

        ItemResponseDto dto = ItemRequestMapper.mapToItemResponseDto(item);

        assertNotNull(dto);
        assertEquals(10L, dto.getItemId());
        assertEquals("Drill", dto.getName());
        assertEquals(3L, dto.getOwnerId());
    }
}