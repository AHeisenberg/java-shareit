package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntTest {
    private final UserService userService;
    private final User mockUser = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();


    @Test
    void testFindUserById() throws ObjectNotFoundException {
        userService.createUser(mockUser);

        User user = userService.findUserById(1L);

        assertThat(user.getId(), equalTo(mockUser.getId()));
        assertThat(user.getName(), equalTo(mockUser.getName()));
        assertThat(user.getEmail(), equalTo(mockUser.getEmail()));
    }

    @Test
    void testFindUser_WrongId() {
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.findUserById(1L));
        assertEquals("User with id 1 does not exist", exception.getMessage());
    }
}
