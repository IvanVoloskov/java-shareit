package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository {
    Item save(Item item); // Добавить/обновить вещь
    List<Item> findAll();
    Optional<Item> findById(Long id);        // Найти по id
    List<Item> findAllByOwnerId(Long ownerId); // Все вещи владельца
    void deleteById(Long id);                 // Удалить
    boolean existsById(Long id);
}