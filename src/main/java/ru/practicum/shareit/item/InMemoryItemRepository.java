package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();
    private long idCounter = 1;

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter);
            idCounter++;
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        List<Item> itemsByOwner = new ArrayList<>();
        for (Item item : items.values()) {
            User owner = item.getOwner();
            if (owner != null && owner.getId() == ownerId) {
                itemsByOwner.add(item);
            }
        }
        return itemsByOwner;
    }

    @Override
    public void deleteById(Long id) {
        for (Item item : items.values()) {
            if (id.equals(item.getId())) {
                items.remove(id);
            }
        }
    }

    @Override
    public boolean existsById(Long id) {
        return items.containsKey(id);
    }
}
