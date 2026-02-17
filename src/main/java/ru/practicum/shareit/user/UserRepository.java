package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    List<User> findAll();
    User save(User user);
    Optional<User> findById(long id);
    void deleteById(long id);
    boolean existsById(long id);
}
