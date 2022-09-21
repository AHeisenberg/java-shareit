package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item createItem(long userId, Item item) throws ValidationException;

    Item findItemById(long userId, long itemId) throws ObjectNotFoundException;

    Collection<Item> findAllByUserId(long userId, int from, int size) throws ObjectNotFoundException;

    Item updateItem(long userId, long itemId, Item item) throws ObjectNotFoundException;

    void deleteItem(long userId, long itemId) throws ObjectNotFoundException;

    Collection<Item> searchItemByText(String text, int from, int size);

    Comment addComment(long userId, long itemId, Comment comment)
            throws ValidationException;

}