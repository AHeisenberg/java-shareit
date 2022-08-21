package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exc.EmailAlreadyExistsException;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class UserRepositoryImpl implements UserRepository {

    private static Long userId = 0L;

    private static Long generateUserId() {
        return ++userId;
    }

    private final Map<Long, User> users = new HashMap<>();


    @Override
    public User createUser(User user) {
        user.setId(generateUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public Map<Long, User> findAllUsers() {
        return users;
    }

    @Override
    public User updateUser(Long userId, User user) {
        User userUpdated = findUserById(userId);

        if (user.getEmail() != null) {
            userUpdated.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            userUpdated.setName(user.getName());
        }

        return userUpdated;
    }

    @Override
    public Long deleteUser(Long userId) {
        return users.remove(userId).getId();
    }

    @Override
    public void checkUserId(Long userId) throws ObjectNotFoundException {
        if (!findAllUsers().containsKey(userId)) {
            throw new ObjectNotFoundException(String.format("User with id %d does not exist", userId),
                    "CheckUserId");
        }
    }

    @Override
    public void checkEmail(String email) throws ValidationException {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException("Email not sent", "CheckEmail");
        }

        for (User user : findAllUsers().values()) {
            if (Objects.equals(user.getEmail(), email)) {
                throw new EmailAlreadyExistsException(String.format("User with email %s already exists", email),
                        "CheckEmail");
            }
        }
    }
}
