package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId() == 0) {
            user.setId(idCounter++);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }

    @Override
    public boolean existsById(long id) {
        return users.containsKey(id);
    }
}