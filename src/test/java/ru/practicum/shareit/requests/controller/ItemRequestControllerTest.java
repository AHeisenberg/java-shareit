package ru.practicum.shareit.requests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final User mockUser = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();
    private final ItemRequestDto.User mockUserDto = ItemRequestDto.User.builder().id(1L).name("UserDTO")
            .email("UserDTO@host.com").build();
    private final ItemRequestDto mockItemRequestDto = ItemRequestDto.builder().id(1L).description("ItemRequestDesk")
            .requestor(mockUserDto).created(LocalDateTime.now()).build();
    private final ItemRequest mockItemRequest = ItemRequest.builder().id(1L).description("ItemRequestDesk")
            .requestor(mockUser).created(LocalDateTime.now()).build();


    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(Long.class), any())).thenReturn(mockItemRequest);
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(mockItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(mockItemRequestDto.getRequestor()
                        .getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(mockItemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(mockItemRequestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testCreateItemRequest_FailedValidation() throws Exception {
        when(itemRequestService.createItemRequest(any(Long.class), any()))
                .thenThrow(new ValidationException("TestCreateItemRequestFailValidation",
                        "TestCreateItemRequestFailValidation"));

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(mockItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("TestCreateItemRequestFailValidation")));
    }

    @Test
    void testFindItemRequestsByUserId() throws Exception {
        when(itemRequestService.findItemRequestsByUser(any(Long.class))).thenReturn(List.of(mockItemRequest));
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestor.id", is(mockItemRequestDto.getRequestor()
                        .getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestor.name", is(mockItemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.[0].requestor.email", is(mockItemRequestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testFindAllItemRequest() throws Exception {
        when(itemRequestService.findAllItemRequest(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(mockItemRequest));
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestor.id", is(mockItemRequestDto.getRequestor()
                        .getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestor.name", is(mockItemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.[0].requestor.email", is(mockItemRequestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testFindItemRequestById() throws Exception {
        when(itemRequestService.findItemRequestById(any(Long.class), any(Long.class))).thenReturn(mockItemRequest);
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mockMvc.perform(get("/requests/1")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(mockItemRequestDto.getRequestor()
                        .getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(mockItemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(mockItemRequestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
