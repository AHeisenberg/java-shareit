package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

public interface ItemRepository {
    Item createItem(Long userId, Item item, User user);

    Item findItemById(Long itemId);

    Collection<Item> findAllByUserId(Long userId);

    Map<Long, Item> findAll();

    Item updateItem(Long itemId, Item item);

    Long deleteItem(Long itemId);

    Collection<Item> searchItemByText(String text);

    boolean checkOwner(Long userId, Long itemId);

    void checkItemId(Long itemId) throws ObjectNotFoundException;
}
