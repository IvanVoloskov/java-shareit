package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(Long userId, ItemCreateRequestDto createDto);

    List<ItemRequestDto> getUserRequest(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestDto getRequestById(Long userId, Long requestId);


}
