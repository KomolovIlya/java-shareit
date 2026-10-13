package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> search(String text);

    void delete(Long id);
}