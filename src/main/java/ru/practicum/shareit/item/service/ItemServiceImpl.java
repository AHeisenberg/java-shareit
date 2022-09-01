package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;



    @Override
    public Item createItem(Long userId, Item item) throws ValidationException {
        User user = userService.findUserById(userId);

        if (!StringUtils.hasText(item.getName())) {
            throw new ValidationException("Name field is not filled in", "CreateItem");
        }

        if (item.getDescription() == null) {
            throw new ValidationException("Description field is not filled in", "CreateItem");
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Available field is not filled in", "CreateItem");
        }
        item.setOwner(user);

        Item itemCreated = itemRepository.save(item);

        log.info("The item with the id {} is created", itemCreated.getId());
        return itemCreated;
    }

    @Override
    public Item findItemById(Long userId, Long itemId) throws ObjectNotFoundException {
        userService.checkUserId(userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(
                String.format("Item with id %d does not exist", itemId),
                "getItemById"
        ));

        if (item.getOwner().getId() == userId) {
            setBookings(item);
        }

        return item;
    }

    @Override
    public Collection<Item> findAllByUserId(Long userId) throws ObjectNotFoundException {
        userService.checkUserId(userId);

        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(this::setBookings)
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) throws ObjectNotFoundException {
        userService.checkUserId(userId);
        Item itemUpdated = findItemById(userId, itemId);

        if (itemUpdated.getOwner().getId() != userId) {
            throw new ObjectNotFoundException("Passed on to the wrong owner of an item", "UpdateItem");
        }

        Optional.ofNullable(item.getName()).ifPresent(itemUpdated::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(itemUpdated::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(itemUpdated::setAvailable);

        log.info("Updated these items with id {}", itemUpdated.getId());
        return itemRepository.save(itemUpdated);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) throws ObjectNotFoundException {
        userService.checkUserId(userId);
        checkItemExistsById(itemId);

        itemRepository.deleteById(itemId);

        log.info("Deleted item with id {}", itemId);
    }

    @Override
    public Collection<Item> searchItemByText(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }

        return itemRepository.search(text);
    }

    @Override
    public Comment addComment(Long userId, Long itemId, Comment comment)
            throws ValidationException {
        User user = userService.findUserById(userId);
        Item item = findItemById(userId, itemId);

        bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(userId, itemId, BookingStatus.APPROVED,
                        LocalDateTime.now())
                .orElseThrow(() -> new ValidationException(
                        String.format("The user with id %d did not take the item with id %d on lease", userId, itemId),
                        "GetBookingById"
                ));

        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        log.info("Comment created with id {}", comment.getId());
        return commentRepository.save(comment);
    }

    @Override
    public void checkItemExistsById(Long itemId) throws ObjectNotFoundException {
        if (!itemRepository.existsById(itemId)) {
            throw new ObjectNotFoundException(
                    String.format("Item with id %d does not exist", itemId),
                    "CheckItemExistsById"
            );
        }
    }

    private Item setBookings(Item item) {
        Optional<Booking> last = getLastBookingForItem(item.getOwner().getId());
        Optional<Booking> next = getNextBookingForItem(item.getOwner().getId());

        item.setLastBooking(last.orElse(null));
        item.setNextBooking(next.orElse(null));

        return item;
    }

    private Optional<Booking> getLastBookingForItem(long userId) {
        return bookingRepository.findFirstByItemOwnerIdAndStatusOrderByEnd(userId,
                BookingStatus.APPROVED);
    }

    private Optional<Booking> getNextBookingForItem(long userId) {
        return bookingRepository.findFirstByItemOwnerIdAndStatusOrderByEndDesc(userId,
                BookingStatus.APPROVED);
    }
}