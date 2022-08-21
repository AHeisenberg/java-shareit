package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto) throws ValidationException;

    ItemDto findItemById(Long itemId) throws ObjectNotFoundException;

    Collection<ItemDto> findAllItemsByUserId(Long userId) throws ObjectNotFoundException;

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) throws ObjectNotFoundException;

    Long deleteItem(Long userId, Long itemId) throws ObjectNotFoundException;

    Collection<ItemDto> searchItemByText(String text);
}
