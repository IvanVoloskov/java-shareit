package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemCreateRequestDto createDto) {
        log.info("Создание запроса от пользователя {}", userId);
        User requestor  = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequestMapper.mapToEntity(createDto, requestor);
        ItemRequest savedRequest = itemRequestRepository.save(request);
        log.info("Запрос создан с id {}", savedRequest.getId());
        return ItemRequestMapper.mapToDto(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserRequest(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw  new NotFoundException("Пользователь с ID " + userId + " не найден!");
        }

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.mapToDto(request);
                    List<Item> requestItems = itemsByRequest.getOrDefault(request.getId(), List.of());
                    List<ItemResponseDto> itemDtos = requestItems.stream()
                            .map(ItemRequestMapper::mapToItemResponseDto)
                            .collect(Collectors.toList());
                    dto.setItems(itemDtos);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден!");
        }

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> allItems = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequest = allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        List<ItemRequestDto> result = new ArrayList<>();

        for (ItemRequest request : requests) {
            ItemRequestDto dto = ItemRequestMapper.mapToDto(request);
            List<Item> requestItems = itemsByRequest.getOrDefault(request.getId(), Collections.emptyList());
            List<ItemResponseDto> itemDtos = new ArrayList<>();
            for (Item item : requestItems) {
                ItemResponseDto itemDto = ItemRequestMapper.mapToItemResponseDto(item);
                itemDtos.add(itemDto);
            }

            dto.setItems(itemDtos);

            result.add(dto);
        }

        return result;

    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден!");
        }

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<Item> requestItems = itemRepository.findAllByRequestId(requestId);

        ItemRequestDto dto = ItemRequestMapper.mapToDto(request);

        List<ItemResponseDto> itemResponseDtos = new ArrayList<>();
        for (Item item : requestItems) {
            ItemResponseDto itemDto = ItemRequestMapper.mapToItemResponseDto(item);
            itemResponseDtos.add(itemDto);
        }

        dto.setItems(itemResponseDtos);

        return dto;
    }

}
