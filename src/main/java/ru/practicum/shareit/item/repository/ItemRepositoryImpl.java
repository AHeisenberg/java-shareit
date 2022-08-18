package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static Long itemId = 0L;

    private static Long generateItemId() {
        return ++itemId;
    }

    private final Map<Long, Item> items = new HashMap<>();


    @Override
    public Item createItem(Long userId, Item item, User user) {
        item.setId(generateItemId());
        item.setOwner(user);

        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> findAllByUserId(Long userId) {
        return findAll()
                .values()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Item> findAll() {
        return items;
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        Item itemUpdated = findItemById(itemId);

        if (item.getName() != null) {
            itemUpdated.setName(item.getName());
        }

        if (item.getDescription() != null) {
            itemUpdated.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemUpdated.setAvailable(item.getAvailable());
        }

        return itemUpdated;
    }

    @Override
    public Long deleteItem(Long itemId) {
        return items.remove(itemId).getId();
    }

    @Override
    public Collection<Item> searchItemByText(String text) {
        return findAll()
                .values()
                .stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && i.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkOwner(Long userId, Long itemId) {
        return !items.get(itemId).getOwner().getId().equals(userId);
    }

    @Override
    public void checkItemId(Long itemId) throws ObjectNotFoundException {
        if (!findAll().containsKey(itemId)) {
            throw new ObjectNotFoundException(String.format("Item with id %d does not exist", itemId), "CheckItemId");
        }
    }
}
