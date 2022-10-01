package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final UserDto mockUserDto = UserDto.builder().id(1L).name("UserDto").email("UserDto@host.com").build();
    private final User mockUser = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();


    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any())).thenReturn(mockUser);
        doReturn(mockUserDto).when(userMapper).toUserDto(any());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(mockUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(mockUserDto.getName())))
                .andExpect(jsonPath("$.email", is(mockUserDto.getEmail())));
    }

    @Test
    void testFindUserById() throws Exception {
        when(userService.findUserById(any(Long.class))).thenReturn(mockUser);
        doReturn(mockUserDto).when(userMapper).toUserDto(any());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(mockUserDto.getName())))
                .andExpect(jsonPath("$.email", is(mockUserDto.getEmail())));
    }

    @Test
    void testFindAllUsers() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(mockUser));
        doReturn(mockUserDto).when(userMapper).toUserDto(any());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(mockUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(mockUserDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(mockUserDto.getEmail())));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(any(Long.class), any())).thenReturn(mockUser);
        doReturn(mockUserDto).when(userMapper).toUserDto(any());

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(mockUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(mockUserDto.getName())))
                .andExpect(jsonPath("$.email", is(mockUserDto.getEmail())));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
    }
}
