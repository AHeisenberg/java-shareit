package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserRepository {
    User createUser(User user);

    User findUserById(Long userId);

    Map<Long, User> findAllUsers();

    User updateUser(Long userId, User user);

    Long deleteUser(Long userId);

    void checkUserId(Long userId) throws ObjectNotFoundException;

    void checkEmail(String email) throws ValidationException;
}
