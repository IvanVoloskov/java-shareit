package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemIdOrderByCreatedDesc(Long itemId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = ?1 " +
            "AND b.booker.id = ?2 AND b.status = 'APPROVED' AND b.end < CURRENT_TIMESTAMP")
    Boolean hasUserBookedAndFinished(Long itemId, Long userId);
}
