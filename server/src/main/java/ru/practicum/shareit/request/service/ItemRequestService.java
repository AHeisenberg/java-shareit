package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequest createItemRequest(long userId, ItemRequest toItem) throws ValidationException;

    Collection<ItemRequest> findItemRequestsByUser(long userId) throws ObjectNotFoundException;

    Collection<ItemRequest> findAllItemRequest(long userId, int from, int size) throws ObjectNotFoundException;

    ItemRequest findItemRequestById(long userId, long requestId) throws ObjectNotFoundException;

    void checkItemRequestExistsById(long requestId) throws ObjectNotFoundException;
}
