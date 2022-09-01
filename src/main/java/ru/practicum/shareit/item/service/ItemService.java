package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item createItem(Long userId, Item item) throws ValidationException;

    Item findItemById(Long userId, Long itemId) throws ObjectNotFoundException;

    Collection<Item> findAllByUserId(Long userId) throws ObjectNotFoundException;

    Item updateItem(Long userId, Long itemId, Item item) throws ObjectNotFoundException;

    void deleteItem(Long userId, Long itemId) throws ObjectNotFoundException;

    Collection<Item> searchItemByText(String text);

    Comment addComment(Long userId, Long itemId, Comment comment)
            throws ValidationException;

    void checkItemExistsById(Long itemId) throws ObjectNotFoundException;
}