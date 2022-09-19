package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exc.InvalidParamException;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;
    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto)
            throws ValidationException {
        ItemRequest itemRequest = itemRequestService.createItemRequest(userId,
                itemRequestMapper.toItemRequest(itemRequestDto));

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @GetMapping
    public Collection<ItemRequestDto> findItemRequestsByUserId(@RequestHeader(HEADER_USER_ID) long userId)
            throws ObjectNotFoundException {
        return itemRequestService.findItemRequestsByUser(userId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "20") int size)
            throws ObjectNotFoundException, InvalidParamException {
        return itemRequestService.findAllItemRequest(userId, from, size)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findItemRequestById(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long requestId)
            throws ObjectNotFoundException {
        return itemRequestMapper.toItemRequestDto(itemRequestService.findItemRequestById(userId, requestId));
    }
}
