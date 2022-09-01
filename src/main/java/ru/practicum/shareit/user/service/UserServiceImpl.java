package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        user = userRepository.save(user);

        log.info("User with id {} is created", user.getId());
        return user;
    }

    @Override
    public User findUserById(Long userId) throws ObjectNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(
                String.format("User with id %d does not exist", userId),
                "GetUserById")
        );
    }

    @Override
    public Collection<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long userId, User user) throws ObjectNotFoundException {
        User userUpdated = findUserById(userId);

        Optional.ofNullable(user.getEmail()).ifPresent(userUpdated::setEmail);
        Optional.ofNullable(user.getName()).ifPresent(userUpdated::setName);

        log.info("Updated user data with id {}", userUpdated.getId());
        return userRepository.save(userUpdated);
    }

    @Override
    public void deleteUser(Long userId) throws ObjectNotFoundException {
        checkUserId(userId);

        userRepository.deleteById(userId);

        log.info("Deleted user with id {}", userId);
    }

    @Override
    public void checkUserId(Long userId) throws ObjectNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(
                    String.format("User with id %d does not exist", userId),
                    "CheckUserExistsById"
            );
        }
    }
}
