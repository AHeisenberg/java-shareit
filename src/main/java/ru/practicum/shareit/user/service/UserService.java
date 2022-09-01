package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User createUser(User user);

    User findUserById(Long userId) throws ObjectNotFoundException;

    Collection<User> findAllUsers();

    User updateUser(Long userId, User user) throws ObjectNotFoundException;

    void deleteUser(Long userId) throws ObjectNotFoundException;

    void checkUserId(Long userId) throws ObjectNotFoundException;
}
