package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository requestRepository;

    private User user1;
    private User user2;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@mail.com");
        em.persist(user1);

        user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@mail.com");
        em.persist(user2);

        request1 = new ItemRequest();
        request1.setDescription("Need a drill");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        em.persist(request1);

        request2 = new ItemRequest();
        request2.setDescription("Need a hammer");
        request2.setRequestor(user2);
        request2.setCreated(LocalDateTime.now());
        em.persist(request2);
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_ShouldReturnUserRequests() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(user1.getId());

        assertEquals(1, requests.size());
        assertEquals("Need a drill", requests.get(0).getDescription());
        assertEquals(user1.getId(), requests.get(0).getRequestor().getId());
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_ShouldReturnEmpty_WhenNoRequests() {
        User user3 = new User();
        user3.setName("User 3");
        user3.setEmail("user3@mail.com");
        em.persist(user3);

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(user3.getId());

        assertTrue(requests.isEmpty());
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_ShouldReturnOtherUsersRequests() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user1.getId());

        assertEquals(1, requests.size());
        assertEquals("Need a hammer", requests.get(0).getDescription());
        assertEquals(user2.getId(), requests.get(0).getRequestor().getId());
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_ShouldBeSortedDesc() {
        // Создаем еще один запрос от user2
        ItemRequest request3 = new ItemRequest();
        request3.setDescription("Another request");
        request3.setRequestor(user2);
        request3.setCreated(LocalDateTime.now().plusHours(1));
        em.persist(request3);

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user1.getId());

        assertEquals(2, requests.size());
        assertTrue(requests.get(0).getCreated().isAfter(requests.get(1).getCreated()));
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_ShouldReturnEmpty_WhenNoOtherRequests() {
        em.remove(request2);

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user1.getId());

        assertTrue(requests.isEmpty());
    }
}