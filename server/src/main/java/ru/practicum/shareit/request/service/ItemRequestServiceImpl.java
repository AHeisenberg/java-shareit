package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.trait.PageTrait;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService, PageTrait {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequest createItemRequest(long userId, ItemRequest itemRequest) throws ValidationException {
        User user = userService.findUserById(userId);
        validateItemRequest(itemRequest);

        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest itemRequestCreated = itemRequestRepository.save(itemRequest);

        log.info("createItemRequest. Created a request for an item with id {}", itemRequestCreated.getId());
        return itemRequestCreated;
    }

    @Override
    public Collection<ItemRequest> findItemRequestsByUser(long userId) throws ObjectNotFoundException {
        userService.checkUserId(userId);

        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
    }

    @Override
    public Collection<ItemRequest> findAllItemRequest(long userId, int from, int size)
            throws ObjectNotFoundException {
        userService.checkUserId(userId);

        Pageable page = getPage(from, size, "created", Sort.Direction.ASC);

        return itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, page);
    }

    @Override
    public ItemRequest findItemRequestById(long userId, long requestId) throws ObjectNotFoundException {
        userService.checkUserId(userId);

        return itemRequestRepository.findById(
                requestId).orElseThrow(() -> new ObjectNotFoundException(
                        String.format("No request for an item with id %d", requestId),
                        "GetItemRequestById"
                )
        );
    }

    @Override
    public void checkItemRequestExistsById(long requestId) throws ObjectNotFoundException {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new ObjectNotFoundException(
                    String.format("No request for an item with id %d", requestId),
                    "CheckUserExistsItemRequestById"
            );
        }
    }

    private void validateItemRequest(ItemRequest itemRequest) throws ValidationException {
        if (itemRequest.getDescription() == null) {
            throw new ValidationException("The description field is empty", "CreateItem");
        }
    }
}
