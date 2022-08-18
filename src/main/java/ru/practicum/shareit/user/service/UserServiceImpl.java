package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) throws ValidationException {
        userRepository.checkEmail(userDto.getEmail());

        User user = userRepository.createUser(userMapper.toUser(userDto));

        log.info("User with id {} is created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto findUserById(Long userId) throws ObjectNotFoundException {
        checkUserId(userId);

        User user = userRepository.findUserById(userId);
        return userMapper.toUserDto(user);
    }

    public Collection<UserDto> findAllUsers() {
        return userRepository.findAllUsers().values().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) throws ValidationException {
        checkUserId(userId);

        if (StringUtils.hasText(userDto.getEmail())) {
            userRepository.checkEmail(userDto.getEmail());
        }

        User user = userRepository.updateUser(userId, userMapper.toUser(userDto));
        log.info("Updated user data with id {}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public Long deleteUser(Long userId) throws ObjectNotFoundException {
        checkUserId(userId);
        Long userDeletedId = userRepository.deleteUser(userId);

        log.info("Deleted user with id {}", userDeletedId);
        return userDeletedId;
    }

    @Override
    public void checkUserId(Long userId) throws ObjectNotFoundException {
        userRepository.checkUserId(userId);
    }
}
