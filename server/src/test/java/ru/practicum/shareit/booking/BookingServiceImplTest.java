package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingCreateDto createDto;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");

        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_ShouldSaveBooking_WhenDataValid() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(2L, createDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Status.WAITING, result.getStatus());
        assertEquals("Drill", result.getItem().getName());
    }

    @Test
    void createBooking_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(99L, createDto));
    }

    @Test
    void createBooking_ShouldThrow_WhenOwnerTriesToBookOwnItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, createDto));
    }

    @Test
    void createBooking_ShouldThrow_WhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(2L, createDto));
    }

    @Test
    void createBooking_ShouldThrow_WhenStartAfterEnd() {
        createDto.setStart(LocalDateTime.now().plusDays(2));
        createDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(2L, createDto));
    }

    @Test
    void createBooking_ShouldThrow_WhenStartEqualsEnd() {
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        createDto.setStart(sameTime);
        createDto.setEnd(sameTime);

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(2L, createDto));
    }

    @Test
    void createBooking_ShouldThrow_WhenStartInPast() {
        createDto.setStart(LocalDateTime.now().minusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(2L, createDto));
    }

    @Test
    void approveBooking_ShouldApprove_WhenOwnerApproves() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingDto result = bookingService.approveBooking(1L, 1L, true);

        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void approveBooking_ShouldReject_WhenOwnerRejects() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingDto result = bookingService.approveBooking(1L, 1L, false);

        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void approveBooking_ShouldThrow_WhenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L, 99L, true));
    }

    @Test
    void approveBooking_ShouldThrow_WhenUserNotOwner() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(2L, 1L, true));
    }

    @Test
    void approveBooking_ShouldThrow_WhenBookingNotWaiting() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void getBookingById_ShouldThrow_WhenUserNotAuthorized() {
        User otherUser = new User();
        otherUser.setId(3L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(3L, 1L));
    }

    @Test
    void getBookingById_ShouldThrow_WhenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 99L));
    }

    @Test
    void getUserBookings_ShouldReturnList_WithStateAll() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(2L)).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getUserBookings(2L, "ALL");

        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_ShouldFilterByStateWaiting() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(2L)).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getUserBookings(2L, "WAITING");

        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_ShouldThrow_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(99L, "ALL"));
    }

    @Test
    void getUserBookings_ShouldThrow_WhenInvalidState() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(2L)).thenReturn(List.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getUserBookings(2L, "INVALID_STATE"));
    }

    @Test
    void getOwnerBookings_ShouldReturnList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerId(1L)).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getOwnerBookings(1L, "ALL");

        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_ShouldThrow_WhenOwnerNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getOwnerBookings(99L, "ALL"));
    }
}