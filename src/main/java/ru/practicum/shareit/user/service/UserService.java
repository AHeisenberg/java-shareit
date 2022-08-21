package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto) throws ValidationException;


    UserDto findUserById(Long userId) throws ObjectNotFoundException;

    Collection<UserDto> findAllUsers();

    UserDto updateUser(Long userId, UserDto userDto)
            throws ValidationException;

    Long deleteUser(Long userId) throws ObjectNotFoundException;


    void checkUserId(Long userId) throws ObjectNotFoundException;
}
