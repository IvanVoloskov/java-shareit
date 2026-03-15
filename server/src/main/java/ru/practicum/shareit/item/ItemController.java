package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.addItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.updateItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> userItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.userItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByDescription(@RequestParam String text) {
        return itemService.getItemsByDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentCreateDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
