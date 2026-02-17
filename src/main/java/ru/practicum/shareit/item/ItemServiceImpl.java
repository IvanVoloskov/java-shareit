package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        validateItemDto(itemDto);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toEntity(itemDto);
        item.setOwner(owner);
        item.setId(null);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        log.info("Обновление предмета. UserId: {}, ItemDto: {}", userId, itemDto);

        if (itemDto.getId() == null) {
            log.error("ID предмета не указан");
            throw new ValidationException("ID предмета должен быть указан");
        }

        Item oldItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> {
                    log.error("Предмет с id {} не найден", itemDto.getId());
                    return new NotFoundException("Вещь с id " + itemDto.getId() + " не найдена");
                });

        log.info("Найден предмет: {}", oldItem);

        if (!oldItem.getOwner().getId().equals(userId)) {
            log.error("Пользователь {} не является владельцем предмета {}", userId, oldItem.getId());
            throw new NotFoundException("Редактировать описание предмета может только её владелец!");
        }

        boolean updated = false;

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            log.info("Обновляем name с {} на {}", oldItem.getName(), itemDto.getName());
            oldItem.setName(itemDto.getName());
            updated = true;
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            log.info("Обновляем description с {} на {}", oldItem.getDescription(), itemDto.getDescription());
            oldItem.setDescription(itemDto.getDescription());
            updated = true;
        }

        if (itemDto.getAvailable() != null) {
            log.info("Обновляем available с {} на {}", oldItem.isAvailable(), itemDto.getAvailable());
            oldItem.setAvailable(itemDto.getAvailable());
            updated = true;
        }

        log.info("Были ли обновления: {}", updated);

        Item updatedItem = itemRepository.save(oldItem);
        log.info("Предмет после обновления: {}", updatedItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> userItems(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));

        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByDescription(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerCaseText = text.toLowerCase();

        List<Item> items = itemRepository.findAll().stream()
                .filter(Item::isAvailable)
                .filter(item ->
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerCaseText)) ||
                                item.getName() != null && item.getName().toLowerCase().contains(lowerCaseText))
                .toList();

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void validateItemDto(ItemDto itemDto) {
        log.info("Валидация ItemDto: {}", itemDto);

        // Проверка name
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.error("Ошибка валидации: название не должно быть пустым");
            throw new ValidationException("Название не должно быть пустым");
        }

        // Проверка description
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.error("Ошибка валидации: описание не должно быть пустым");
            throw new ValidationException("Описание не должно быть пустым");
        }

        // Проверка available
        if (itemDto.getAvailable() == null) {
            log.error("Ошибка валидации: статус доступности должен быть указан");
            throw new ValidationException("Статус доступности должен быть указан");
        }
    }
}
