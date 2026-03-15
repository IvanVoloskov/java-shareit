package ru.practicum.shareit.item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        return itemDto;
    }

    public static Item toEntity(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    public static ItemCreateDto toCreateDto(Item item) {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName(item.getName());
        itemCreateDto.setDescription(item.getDescription());
        itemCreateDto.setAvailable(item.isAvailable());
        itemCreateDto.setRequestId(item.getRequest().getId());
        return itemCreateDto;
    }
}
