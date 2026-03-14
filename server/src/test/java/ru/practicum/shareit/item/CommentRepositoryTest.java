package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        em.persist(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        em.persist(booker);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        comment1 = new Comment();
        comment1.setText("Great tool!");
        comment1.setCreated(LocalDateTime.now().minusDays(1));
        comment1.setItem(item);
        comment1.setAuthor(booker);
        em.persist(comment1);

        comment2 = new Comment();
        comment2.setText("Very useful");
        comment2.setCreated(LocalDateTime.now());
        comment2.setItem(item);
        comment2.setAuthor(booker);
        em.persist(comment2);
    }

    @Test
    void findAllByItemIdOrderByCreatedDesc_ShouldReturnCommentsSorted() {
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());

        assertEquals(2, comments.size());
        assertTrue(comments.get(0).getCreated().isAfter(comments.get(1).getCreated()));
        assertEquals("Very useful", comments.get(0).getText());
        assertEquals("Great tool!", comments.get(1).getText());
    }

    @Test
    void findAllByItemIdOrderByCreatedDesc_ShouldReturnEmpty_WhenNoComments() {
        Item newItem = new Item();
        newItem.setName("Hammer");
        newItem.setDescription("Heavy hammer");
        newItem.setAvailable(true);
        newItem.setOwner(owner);
        em.persist(newItem);

        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(newItem.getId());

        assertTrue(comments.isEmpty());
    }

    @Test
    void hasUserBookedAndFinished_ShouldReturnTrue_WhenUserHasApprovedFinishedBooking() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
        em.persist(booking);

        Boolean result = commentRepository.hasUserBookedAndFinished(item.getId(), booker.getId());

        assertTrue(result);
    }

    @Test
    void hasUserBookedAndFinished_ShouldReturnFalse_WhenBookingIsNotApproved() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        em.persist(booking);

        Boolean result = commentRepository.hasUserBookedAndFinished(item.getId(), booker.getId());

        assertFalse(result);
    }

    @Test
    void hasUserBookedAndFinished_ShouldReturnFalse_WhenBookingNotFinished() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
        em.persist(booking);

        Boolean result = commentRepository.hasUserBookedAndFinished(item.getId(), booker.getId());

        assertFalse(result);
    }

    @Test
    void hasUserBookedAndFinished_ShouldReturnFalse_WhenNoBooking() {
        Boolean result = commentRepository.hasUserBookedAndFinished(item.getId(), 999L);

        assertFalse(result);
    }
}