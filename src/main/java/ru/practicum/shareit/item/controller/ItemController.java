package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER_USER_ID) String userId, @Valid @RequestBody ItemDto itemDto)
            throws  ValidationException {
        return itemService.createItem(Long.valueOf(userId), itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable String  itemId) throws ObjectNotFoundException {
        return itemService.findItemById(Long.valueOf(itemId));
    }

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader(HEADER_USER_ID) String userId) throws ObjectNotFoundException {
        return itemService.findAllItemsByUserId(Long.valueOf(userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) String userId, @PathVariable String itemId,
                              @Valid @RequestBody ItemDto itemDto) throws ObjectNotFoundException {
        return itemService.updateItem(Long.valueOf(userId), Long.valueOf(itemId), itemDto);
    }

    @DeleteMapping("/{itemId}")
    public Long deleteItem(@RequestHeader(HEADER_USER_ID) String userId, @PathVariable String itemId)
            throws ObjectNotFoundException {
        return itemService.deleteItem(Long.valueOf(userId), Long.valueOf(itemId));
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemByText(@RequestParam String text) {
        return itemService.searchItemByText(text);
    }
}
