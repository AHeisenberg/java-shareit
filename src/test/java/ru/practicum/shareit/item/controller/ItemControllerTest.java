package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private ItemService itemService;
    @MockBean
    private ItemMapper itemMapper;
    @MockBean
    private CommentMapper commentMapper;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final User mockUser = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();
    private final Item mockItem = Item.builder().id(1L).name("Item")
            .description("ItemDescription").available(true).owner(mockUser).build();
    private final Comment mockComment = Comment.builder()
            .id(1L).text("Comment").item(mockItem).author(mockUser).created(LocalDateTime.now())
            .build();


    private final ItemDto mockItemDto = ItemDto.builder()
            .id(1L).name("ItemDTO").description("ItemDTODescription").available(true)
            .owner(ItemDto.User.builder().id(1L).build())
            .nextBooking(ItemDto.Booking.builder().id(2L).bookerId(4L).start(LocalDateTime.now().plusDays(4))
                    .end(LocalDateTime.now().plusDays(7)).build()).build();

    private final CommentDto mockCommentDto = CommentDto.builder()
            .id(1L).text("CommentDto").authorName("user").created(LocalDateTime.now()).build();


    @Test
    void testCreateItem() throws Exception {
        when(itemService.createItem(any(Long.class), any())).thenReturn(mockItem);
        doReturn(mockItemDto).when(itemMapper).toItemDto(any());

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(mockItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(mockItemDto.getName())))
                .andExpect(jsonPath("$.description", is(mockItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(mockItemDto.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(mockItemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(mockItemDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(mockItemDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(mockItemDto.getRequestId())))
                .andExpect(jsonPath("$.lastBooking", is(mockItemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking.id", is(mockItemDto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(mockItemDto.getNextBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$.nextBooking.start", is(mockItemDto.getNextBooking().getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.nextBooking.end", is(mockItemDto.getNextBooking().getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.comments", is(mockItemDto.getComments())));
    }

    @Test
    void testFindItemById() throws Exception {
        when(itemService.findItemById(any(Long.class), any(Long.class)))
                .thenReturn(mockItem);
        doReturn(mockItemDto).when(itemMapper).toItemDto(any());

        mockMvc.perform(get("/items/1")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(mockItemDto.getName())))
                .andExpect(jsonPath("$.description", is(mockItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(mockItemDto.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(mockItemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(mockItemDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(mockItemDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(mockItemDto.getRequestId())))
                .andExpect(jsonPath("$.lastBooking", is(mockItemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking.id", is(mockItemDto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(mockItemDto.getNextBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$.nextBooking.start", is(mockItemDto.getNextBooking().getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.nextBooking.end", is(mockItemDto.getNextBooking().getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.comments", is(mockItemDto.getComments())));
    }

    @Test
    void testFindItem_WrongId() throws Exception {
        when(itemService.findItemById(any(Long.class), any(Long.class)))
                .thenThrow(new ObjectNotFoundException("TestGetItemByWrongId", "TestGetItemByWrongId"));

        mockMvc.perform(get("/items/1")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("TestGetItemByWrongId")));
    }

    @Test
    void testFindAllByUserId() throws Exception {
        when(itemService.findAllByUserId(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(mockItem));
        doReturn(mockItemDto).when(itemMapper).toItemDto(any());

        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(mockItemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(mockItemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(mockItemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].owner.id", is(mockItemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.[0].owner.name", is(mockItemDto.getOwner().getName())))
                .andExpect(jsonPath("$.[0].owner.email", is(mockItemDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.[0].requestId", is(mockItemDto.getRequestId())))
                .andExpect(jsonPath("$.[0].lastBooking", is(mockItemDto.getLastBooking())))
                .andExpect(jsonPath("$.[0].nextBooking.id", is(mockItemDto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.bookerId", is(mockItemDto.getNextBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.start", is(mockItemDto.getNextBooking().getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].nextBooking.end", is(mockItemDto.getNextBooking().getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].comments", is(mockItemDto.getComments())));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(any(Long.class), any(Long.class), any()))
                .thenReturn(mockItem);
        doReturn(mockItemDto).when(itemMapper).toItemDto(any());

        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(mockItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(mockItemDto.getName())))
                .andExpect(jsonPath("$.description", is(mockItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(mockItemDto.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(mockItemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(mockItemDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(mockItemDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(mockItemDto.getRequestId())))
                .andExpect(jsonPath("$.lastBooking", is(mockItemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking.id", is(mockItemDto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(mockItemDto.getNextBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$.nextBooking.start", is(mockItemDto.getNextBooking().getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.nextBooking.end", is(mockItemDto.getNextBooking().getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.comments", is(mockItemDto.getComments())));
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchItemByText() throws Exception {
        when(itemService.searchItemByText(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(mockItem));
        doReturn(mockItemDto).when(itemMapper).toItemDto(any());

        mockMvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("text", "searchText"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(mockItemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(mockItemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(mockItemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].owner.id", is(mockItemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.[0].owner.name", is(mockItemDto.getOwner().getName())))
                .andExpect(jsonPath("$.[0].owner.email", is(mockItemDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.[0].requestId", is(mockItemDto.getRequestId())))
                .andExpect(jsonPath("$.[0].lastBooking", is(mockItemDto.getLastBooking())))
                .andExpect(jsonPath("$.[0].nextBooking.id", is(mockItemDto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.bookerId", is(mockItemDto.getNextBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.start", is(mockItemDto.getNextBooking().getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].nextBooking.end", is(mockItemDto.getNextBooking().getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].comments", is(mockItemDto.getComments())));
    }

    @Test
    void testCreateComment() throws Exception {
        when(itemService.addComment(any(Long.class), any(Long.class), any()))
                .thenReturn(mockComment);
        doReturn(mockCommentDto).when(commentMapper).toCommentDto(any());

        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(mockCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(mockCommentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(mockCommentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(mockCommentDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
