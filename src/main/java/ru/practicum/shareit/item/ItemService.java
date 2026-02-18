package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);
    ItemDto updateItem(Long userId, ItemDto itemDto);
    ItemDto getItemById(Long itemId);
    List<ItemDto> userItems(long userId);
    List<ItemDto> getItemsByDescription(String text);
}
