package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User createUser(User user);

    User findUserById(long userId) throws ObjectNotFoundException;

    Collection<User> findAllUsers();

    User updateUser(long userId, User user) throws ObjectNotFoundException;

    void deleteUser(long userId) throws ObjectNotFoundException;

    void checkUserId(long userId) throws ObjectNotFoundException;
}
