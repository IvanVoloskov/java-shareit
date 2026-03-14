package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b JOIN b.item as i WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b JOIN b.item as i WHERE b.item.id = ?1 " +
            "AND b.status = 'APPROVED' AND b.start < ?2 ORDER BY b.start DESC")
    List<Booking> findLastBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE b.item.id = ?1 " +
            "AND b.status = 'APPROVED' AND b.start > ?2 ORDER BY b.start ASC")
    List<Booking> findNextBooking(Long itemId, LocalDateTime now);
}
