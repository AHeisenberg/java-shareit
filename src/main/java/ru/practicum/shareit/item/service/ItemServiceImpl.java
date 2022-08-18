package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final UserMapper userMapper;

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;


    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) throws ValidationException {
        userService.checkUserId(userId);

        if (!StringUtils.hasText(itemDto.getName())) {
            throw new ValidationException("Name field is not filled in", "CreateItem");
        }

        if (itemDto.getDescription() == null) {
            throw new ValidationException("Description field is not filled in", "CreateItem");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available field is not filled in", "CreateItem");
        }

        UserDto userDto = userService.findUserById(userId);
        Item item = itemRepository.createItem(userId, itemMapper.toItem(itemDto), userMapper.toUser(userDto));

        log.info("The item with the id {} is created", item.getId());
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findItemById(Long itemId) throws ObjectNotFoundException {
        itemRepository.checkItemId(itemId);

        Item item = itemRepository.findItemById(itemId);
        return itemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> findAllItemsByUserId(Long userId) throws ObjectNotFoundException {
        userService.checkUserId(userId);

        return itemRepository.findAllByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto)
            throws ObjectNotFoundException {
        userService.checkUserId(userId);
        itemRepository.checkItemId(itemId);

        if (itemRepository.checkOwner(userId, itemId)) {
            throw new ObjectNotFoundException("Passed on to the wrong owner of an item", "UpdateItem");
        }

        Item item = itemRepository.updateItem(itemId, itemMapper.toItem(itemDto));
        log.info("Updated these items with id {}", item.getId());
        return itemMapper.toItemDto(item);
    }

    @Override
    public Long deleteItem(Long userId, Long itemId) throws ObjectNotFoundException {
        userService.checkUserId(userId);
        itemRepository.checkItemId(itemId);

        Long itemDeletedId = itemRepository.deleteItem(itemId);
        log.info("Deleted item with id {}", itemDeletedId);
        return itemDeletedId;
    }

    @Override
    public Collection<ItemDto> searchItemByText(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }

        return itemRepository.searchItemByText(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}