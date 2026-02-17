package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);
    ItemDto updateItem(Long userId, ItemDto itemDto);
    ItemDto getItemById(Long itemId);
    List<ItemDto> userItems(long userId);
    List<ItemDto> getItemsByDescription(String text);
}
