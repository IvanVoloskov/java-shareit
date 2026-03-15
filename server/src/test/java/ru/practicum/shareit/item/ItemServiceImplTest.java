package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User booker;
    private Item item;
    private ItemCreateDto itemCreateDto;
    private ItemDto itemDto;
    private ItemRequest request;
    private Comment comment;
    private CommentCreateDto commentCreateDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");

        item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Drill");
        itemCreateDto.setDescription("Powerful drill");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(null);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequestor(booker);
        request.setCreated(LocalDateTime.now());

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Great tool!");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(LocalDateTime.now());

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Great tool!");

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
    }

    @Test
    void addItem_ShouldSaveItem_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.addItem(1L, itemCreateDto);

        assertNotNull(result);
        assertEquals("Drill", result.getName());
        assertEquals("Powerful drill", result.getDescription());
        assertTrue(result.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void addItem_WithRequestId_ShouldSaveItemWithRequest() {
        itemCreateDto.setRequestId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.addItem(1L, itemCreateDto);

        assertNotNull(result);
        assertEquals("Drill", result.getName());
        verify(requestRepository).findById(1L);
    }

    @Test
    void addItem_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(99L, itemCreateDto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void addItem_ShouldThrow_WhenRequestNotFound() {
        itemCreateDto.setRequestId(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(1L, itemCreateDto));
    }

    @Test
    void addItem_ShouldThrow_WhenOwnerAnswersOwnRequest() {
        request.setRequestor(owner); // Владелец пытается ответить на свой запрос
        itemCreateDto.setRequestId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(ValidationException.class, () -> itemService.addItem(1L, itemCreateDto));
    }

    @Test
    void updateItem_ShouldUpdateName_WhenNameProvided() {
        ItemDto updateDto = new ItemDto();
        updateDto.setId(1L);
        updateDto.setName("New Drill");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.updateItem(1L, updateDto);

        assertEquals("New Drill", result.getName());
        assertEquals("Powerful drill", result.getDescription()); // не изменилось
        assertTrue(result.getAvailable()); // не изменилось
    }

    @Test
    void updateItem_ShouldUpdateDescription_WhenDescriptionProvided() {
        ItemDto updateDto = new ItemDto();
        updateDto.setId(1L);
        updateDto.setDescription("New description");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.updateItem(1L, updateDto);

        assertEquals("New description", result.getDescription());
        assertEquals("Drill", result.getName()); // не изменилось
    }

    @Test
    void updateItem_ShouldUpdateAvailable_WhenAvailableProvided() {
        ItemDto updateDto = new ItemDto();
        updateDto.setId(1L);
        updateDto.setAvailable(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.updateItem(1L, updateDto);

        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_ShouldThrow_WhenIdNotProvided() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Drill"); // ID не установлен!

        assertThrows(ValidationException.class, () -> itemService.updateItem(1L, updateDto));
    }

    @Test
    void updateItem_ShouldThrow_WhenItemNotFound() {
        ItemDto updateDto = new ItemDto();
        updateDto.setId(99L);
        updateDto.setName("New Drill");

        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, updateDto));
    }

    @Test
    void updateItem_ShouldThrow_WhenUserNotOwner() {
        ItemDto updateDto = new ItemDto();
        updateDto.setId(1L);
        updateDto.setName("New Drill");

        User otherOwner = new User();
        otherOwner.setId(2L);

        Item itemWithOtherOwner = new Item();
        itemWithOtherOwner.setId(1L);
        itemWithOtherOwner.setName("Drill");
        itemWithOtherOwner.setOwner(otherOwner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemWithOtherOwner));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, updateDto));
    }

    @Test
    void getItemById_ShouldReturnItem_WhenExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(1L)).thenReturn(List.of());

        ItemDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals("Drill", result.getName());
    }

    @Test
    void getItemById_ShouldReturnItemWithComments_WhenCommentsExist() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(1L)).thenReturn(List.of(comment));

        ItemDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals("Drill", result.getName());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        assertEquals("Great tool!", result.getComments().get(0).getText());
        assertEquals("Booker", result.getComments().get(0).getAuthorName());
    }

    @Test
    void getItemById_ShouldThrow_WhenNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(99L));
    }

    @Test
    void getItemsByDescription_ShouldReturnList_WhenTextValid() {
        when(itemRepository.searchAvailableItemsByDescriptionOrName("drill")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getItemsByDescription("drill");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
    }

    @Test
    void getItemsByDescription_ShouldReturnEmpty_WhenTextBlank() {
        List<ItemDto> result = itemService.getItemsByDescription("   ");

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchAvailableItemsByDescriptionOrName(anyString());
    }

    @Test
    void getItemsByDescription_ShouldReturnEmpty_WhenNoMatches() {
        when(itemRepository.searchAvailableItemsByDescriptionOrName("nonexistent")).thenReturn(List.of());

        List<ItemDto> result = itemService.getItemsByDescription("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_ShouldSaveComment_WhenUserHasBooked() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.hasUserBookedAndFinished(1L, 2L)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(2L, 1L, commentCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Great tool!", result.getText());
        assertEquals("Booker", result.getAuthorName());
        assertNotNull(result.getCreated());
    }

    @Test
    void addComment_ShouldThrow_WhenUserHasNotBooked() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.hasUserBookedAndFinished(1L, 2L)).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.addComment(2L, 1L, commentCreateDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(99L, 1L, commentCreateDto));
    }

    @Test
    void addComment_ShouldThrow_WhenItemNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(2L, 99L, commentCreateDto));
    }

    @Test
    void updateItem_ShouldNotUpdate_WhenAllFieldsNull() {
        ItemDto updateDto = new ItemDto();
        updateDto.setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.updateItem(1L, updateDto);

        assertEquals("Drill", result.getName());
        assertEquals("Powerful drill", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void getItemById_ShouldThrow_WhenItemIdNull() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(null));
    }

    @Test
    void getItemsByDescription_ShouldReturnEmpty_WhenTextNull() {
        List<ItemDto> result = itemService.getItemsByDescription(null);

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchAvailableItemsByDescriptionOrName(any());
    }

    @Test
    void addComment_ShouldThrow_WhenCommentTextNull() {
        CommentCreateDto emptyComment = new CommentCreateDto();
        emptyComment.setText(null);

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.hasUserBookedAndFinished(1L, 2L)).thenReturn(true);

        when(commentRepository.save(any(Comment.class))).thenThrow(new ValidationException("Текст комментария не может быть пустым"));

        assertThrows(ValidationException.class,
                () -> itemService.addComment(2L, 1L, emptyComment));
    }

    @Test
    void addComment_ShouldThrow_WhenCommentTextEmpty() {
        CommentCreateDto emptyComment = new CommentCreateDto();
        emptyComment.setText("");

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.hasUserBookedAndFinished(1L, 2L)).thenReturn(true);

        when(commentRepository.save(any(Comment.class))).thenThrow(new ValidationException("Текст комментария не может быть пустым"));

        assertThrows(ValidationException.class,
                () -> itemService.addComment(2L, 1L, emptyComment));
    }

    @Test
    void addItem_ShouldThrow_WhenNameIsNull() {
        itemCreateDto.setDescription(null);

        assertThrows(ValidationException.class, () -> itemService.addItem(1L, itemCreateDto));
        verify(userRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void addItem_ShouldThrow_WhenDescriptionIsNull() {
        itemCreateDto.setAvailable(null);

        assertThrows(ValidationException.class, () -> itemService.addItem(1L, itemCreateDto));
        verify(userRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void addItem_ShouldThrow_WhenAvailableIsNull() {
        itemCreateDto.setName(null);

        assertThrows(ValidationException.class, () -> itemService.addItem(1L, itemCreateDto));
        verify(userRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemById_WithRequest_ShouldSetRequestId() {
        ItemRequest request = new ItemRequest();
        request.setId(5L);
        item.setRequest(request);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(1L)).thenReturn(List.of());

        ItemDto result = itemService.getItemById(1L);

        assertEquals(5L, result.getRequestId());
    }

    @Test
    void userItems_ShouldReturnEmpty_WhenUserHasNoItems() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of());

        List<ItemDto> result = itemService.userItems(1L);

        assertTrue(result.isEmpty());
    }
}