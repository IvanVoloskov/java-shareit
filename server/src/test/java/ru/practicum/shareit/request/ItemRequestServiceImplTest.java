package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User requester;
    private User otherUser;
    private ItemRequest request;
    private ItemCreateRequestDto createDto;
    private Item item;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setId(1L);
        requester.setName("Requester");
        requester.setEmail("requester@mail.com");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Other");
        otherUser.setEmail("other@mail.com");

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequestor(requester);
        request.setCreated(LocalDateTime.now());

        createDto = new ItemCreateRequestDto();
        createDto.setDescription("Need a drill");

        item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setRequest(request);

        User owner = new User();
        owner.setId(3L);
        item.setOwner(owner);
    }

    @Test
    void createRequest_ShouldSaveRequest_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = requestService.createRequest(1L, createDto);

        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getCreated());
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(99L, createDto));
    }

    @Test
    void getUserRequests_ShouldReturnListWithItems_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(item));

        List<ItemRequestDto> result = requestService.getUserRequest(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Need a drill", result.get(0).getDescription());
        assertNotNull(result.get(0).getItems());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals(1L, result.get(0).getItems().get(0).getItemId());
        assertEquals("Drill", result.get(0).getItems().get(0).getName());
    }

    @Test
    void getUserRequests_ShouldReturnEmptyList_WhenNoRequests() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of());

        List<ItemRequestDto> result = requestService.getUserRequest(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserRequests_ShouldThrow_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.getUserRequest(99L));
    }

    @Test
    void getAllRequests_ShouldReturnOtherUsersRequests_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(item));

        List<ItemRequestDto> result = requestService.getAllRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Need a drill", result.get(0).getDescription());
    }

    @Test
    void getAllRequests_ShouldReturnEmptyList_WhenNoOtherRequests() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1L)).thenReturn(List.of());

        List<ItemRequestDto> result = requestService.getAllRequests(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequests_ShouldThrow_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.getAllRequests(99L));
    }

    @Test
    void getRequestById_ShouldReturnRequestWithItems_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));

        ItemRequestDto result = requestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Drill", result.getItems().get(0).getName());
    }

    @Test
    void getRequestById_ShouldReturnRequestWithEmptyItems_WhenNoItems() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of());

        ItemRequestDto result = requestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getRequestById_ShouldThrow_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(99L, 1L));
    }

    @Test
    void getRequestById_ShouldThrow_WhenRequestNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, 99L));
    }
}