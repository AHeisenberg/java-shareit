package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private MockitoSession mockitoSession;

    @BeforeEach
    void setUp() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        userService = new UserServiceImpl(userRepository);
    }

    @AfterEach
    void finish() {
        mockitoSession.finishMocking();
    }

    private final User firstUser = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();
    private final User firstUserUpdate = User.builder().id(2L).name("FirstUserUpdate").email("FirstUser@host.com").build();
    private final User secondUser = User.builder().id(2L).name("SecondUser").email("SecondUser@host.com").build();

    @Test
    void testCreateUser() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(firstUser);

        User user = userService.createUser(firstUser);

        Mockito.verify(userRepository, times(1)).save(firstUser);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(firstUser.getName()));
        assertThat(user.getEmail(), equalTo(firstUser.getEmail()));
    }

    @Test
    void testFindUserById() throws ObjectNotFoundException {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));

        User user = userService.findUserById(1L);

        Mockito.verify(userRepository, times(1)).findById(1L);

        assertThat(user.getName(), equalTo(firstUser.getName()));
        assertThat(user.getEmail(), equalTo(firstUser.getEmail()));
    }

    @Test
    void testFindUserByWrongId() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.findUserById(1L));

        assertEquals("User with id 1 does not exist", exception.getMessage());
    }

    @Test
    void testFindAll() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(firstUser, secondUser));

        Collection<User> users = userService.findAllUsers();

        Mockito.verify(userRepository, times(1)).findAll();

        assertThat(users, hasSize(2));
        assertThat(users, equalTo(List.of(firstUser, secondUser)));
    }

    @Test
    void testUpdateUser() throws ObjectNotFoundException {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(firstUserUpdate);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));

        firstUser.setName("User1Update");

        User user = userService.updateUser(firstUser.getId(), firstUser);

        Mockito.verify(userRepository, times(1)).save(firstUser);

        assertThat(user.getId(), equalTo(firstUserUpdate.getId()));
        assertThat(user.getName(), equalTo(firstUserUpdate.getName()));
    }

    @Test
    void testDeleteUser() throws ObjectNotFoundException {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.deleteUser(1L);

        Mockito.verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCheckUserId() throws ObjectNotFoundException {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.checkUserId(1L);

        Mockito.verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void testCheckUser_IdNotExist() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);

        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.checkUserId(1L));

        assertEquals("User with id 1 does not exist", exception.getMessage());
    }
}
