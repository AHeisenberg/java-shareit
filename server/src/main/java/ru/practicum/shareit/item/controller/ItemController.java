package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER_USER_ID) long userId, @Valid @RequestBody ItemDto itemDto)
            throws ValidationException {
        Item item = itemService.createItem(userId, itemMapper.toItem(itemDto));

        return itemMapper.toItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId)
            throws ObjectNotFoundException {
        return itemMapper.toItemDto((itemService.findItemById(userId, itemId)));
    }

    @GetMapping
    public Collection<ItemDto> findAllByUserId(@RequestHeader(HEADER_USER_ID) long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "20") int size)
            throws ObjectNotFoundException {

        return itemService.findAllByUserId(userId, from, size)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) throws ObjectNotFoundException {
        Item item = itemService.updateItem(userId, itemId, itemMapper.toItem(itemDto));

        return itemMapper.toItemDto(item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId)
            throws ObjectNotFoundException {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemByText(@RequestParam String text,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "20") int size) {
        return itemService.searchItemByText(text, from, size)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto)
            throws ValidationException {
        Comment comment = itemService.addComment(userId, itemId, commentMapper.toComment(commentDto));

        return commentMapper.toCommentDto(comment);
    }
}
