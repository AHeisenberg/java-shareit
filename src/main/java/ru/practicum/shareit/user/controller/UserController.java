package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.createUser(userMapper.toUser(userDto));

        return userMapper.toUserDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable long userId) throws ObjectNotFoundException {
        return userMapper.toUserDto((userService.findUserById(userId)));
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto)
            throws ObjectNotFoundException {
        User user = userService.updateUser(userId, userMapper.toUser(userDto));

        return userMapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) throws ObjectNotFoundException {
        userService.deleteUser(userId);
    }
}
