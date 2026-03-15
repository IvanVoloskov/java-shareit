package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@mail.com");
        em.persist(user);

        Optional<User> found = userRepository.findByEmail("test@mail.com");

        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailNotExists() {
        Optional<User> found = userRepository.findByEmail("nonexistent@mail.com");

        assertTrue(found.isEmpty());
    }
}