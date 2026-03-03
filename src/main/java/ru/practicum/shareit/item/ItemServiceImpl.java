package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        validateItemDto(itemDto);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toEntity(itemDto);
        item.setOwner(owner);
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

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            log.info("Обновляем name с {} на {}", oldItem.getName(), itemDto.getName());
            oldItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            log.info("Обновляем description с {} на {}", oldItem.getDescription(), itemDto.getDescription());
            oldItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            log.info("Обновляем available с {} на {}", oldItem.isAvailable(), itemDto.getAvailable());
            oldItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(oldItem);
        log.info("Предмет после обновления: {}", updatedItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<CommentDto> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(itemId).stream()
                .map(CommentMapper::mapToDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);

        if (item.getOwner().getId().equals(itemId)) {
            getBookingDates(itemDto, itemId);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> userItems(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);
                    List<CommentDto> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId())
                            .stream()
                            .map(CommentMapper::mapToDto)
                            .collect(Collectors.toList());
                    dto.setComments(comments);
                    getBookingDates(dto, item.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByDescription(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.searchAvailableItemsByDescriptionOrName(text);

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentCreateDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!commentRepository.hasUserBookedAndFinished(itemId, userId)) {
            throw new ValidationException("Оставлять отзыв может только тот, кто брал её в аренду");
        }
        Comment comment = CommentMapper.mapToCreateComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.mapToDto(savedComment);
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

    private void getBookingDates(ItemDto itemDto, long itemId) {
        LocalDateTime now = LocalDateTime.now();
        // Получить последнее бронирование
        List<Booking> lastBookings = bookingRepository.findLastBooking(itemId, now);
        if (!lastBookings.isEmpty()) {
            Booking lastBooking = lastBookings.get(0);
            itemDto.setLastBooking(BookingMapper.mapToShortDto(lastBooking));
        }

        // Получить ближайшую бронь
        List<Booking> nextBookings = bookingRepository.findNextBooking(itemId, now);
        if (!nextBookings.isEmpty()) {
            Booking nextBooking = nextBookings.get(0);
            itemDto.setNextBooking(BookingMapper.mapToShortDto(nextBooking));
        }
    }
}
