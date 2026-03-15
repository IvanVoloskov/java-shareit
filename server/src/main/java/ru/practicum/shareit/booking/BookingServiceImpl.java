package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(Long userId, BookingCreateDto bookingCreateDTO) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingCreateDTO.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (!item.isAvailable()) {
            throw new ValidationException("Вещь уже забронирована");
        }

        if (bookingCreateDTO.getStart().isAfter(bookingCreateDTO.getEnd())) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }

        if (bookingCreateDTO.getStart().equals(bookingCreateDTO.getEnd())) {
            throw new ValidationException("Даты начала и окончания не могут совпадать");
        }

        if (bookingCreateDTO.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }

        Booking booking = BookingMapper.mapToCreate(bookingCreateDTO);
        booking.setItem(item);
        booking.setBooker(booker);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.mapToDto(savedBooking);
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Подтверждать бронирование может только владелец вещи");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Бронирование уже в обработке");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        Booking approveBooking = bookingRepository.save(booking);
        return BookingMapper.mapToDto(approveBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Получить данные может только автор бронирования или владелец вещи");
        }

        return BookingMapper.mapToDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);

        return filterBookingsByState(bookings, state).stream()
                .map(BookingMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(ownerId);

        return filterBookingsByState(bookings, state).stream()
                .map(BookingMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "ALL":
                return bookings;
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(b -> b.getStatus() == Status.WAITING)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(b -> b.getStatus() == Status.REJECTED)
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Параметр state: " + state + " неизвестный");
        }
    }
}
