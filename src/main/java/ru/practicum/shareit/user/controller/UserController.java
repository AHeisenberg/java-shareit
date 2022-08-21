package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) throws ValidationException {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable String userId) throws ObjectNotFoundException {
        return userService.findUserById(Long.valueOf(userId));
    }

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable String userId, @Valid @RequestBody UserDto userDto)
            throws ValidationException {
        return userService.updateUser(Long.valueOf(userId), userDto);
    }

    @DeleteMapping("/{userId}")
    public Long deleteUser(@PathVariable String userId) throws ObjectNotFoundException {
        return userService.deleteUser(Long.valueOf(userId));
    }

}
