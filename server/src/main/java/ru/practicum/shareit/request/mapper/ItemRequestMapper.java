package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest item) {
        return new ItemRequestDto(
                item.getId(),
                item.getDescription(),
                toUserItemRequest(item.getRequestor()),
                item.getCreated(),
                item.getItems().stream().map(this::toItemItemRequest).collect(Collectors.toList())
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                Optional.ofNullable(itemRequestDto.getRequestor()).map(this::toUser).orElse(null),
                itemRequestDto.getCreated(),
                itemRequestDto.getItems().stream().map(this::toItem).collect(Collectors.toList())

        );
    }

    private ItemRequestDto.User toUserItemRequest(User user) {
        return new ItemRequestDto.User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private User toUser(ItemRequestDto.User bookingUser) {
        return new User(
                bookingUser.getId(),
                bookingUser.getName(),
                bookingUser.getEmail()
        );
    }

    private ItemRequestDto.Item toItemItemRequest(Item item) {
        return new ItemRequestDto.Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId()
        );
    }

    private Item toItem(ItemRequestDto.Item itemItemRequestDto) {
        return new Item(
                itemItemRequestDto.getId(),
                itemItemRequestDto.getName(),
                itemItemRequestDto.getDescription(),
                itemItemRequestDto.getAvailable(),
                null,
                new ItemRequest(itemItemRequestDto.getId(), null, null, null, null),
                null,
                null,
                null
        );
    }
}
