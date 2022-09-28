package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final ItemDto mockItemDto = ItemDto.builder()
            .id(1L).name("ItemDTO").description("ItemDTODescription").available(true)
            .owner(1L).nextBooking(ItemDto.Booking.builder().id(2L).bookerId(4L)
                    .start(LocalDateTime.now().plusDays(4))
                    .end(LocalDateTime.now().plusDays(7)).build()).build();
    private final CommentDto mockCommentDto = CommentDto.builder()
            .id(1L).text("CommentDto").authorName("user").created(LocalDateTime.now()).build();


    @Test
    void testCreateItem_WithoutUser() throws Exception {
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(mockItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateItem_NameIsEmpty() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L).name("").description("ItemDTODescription").available(true)
                .owner(1L).nextBooking(ItemDto.Booking.builder().id(2L).bookerId(4L)
                        .start(LocalDateTime.now().plusDays(4))
                        .end(LocalDateTime.now().plusDays(7)).build()).build();

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchItemByText_SizeIsIncorrect() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("text", "searchText")
                        .queryParam("from", "0")
                        .queryParam("size", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchItemByText_FromIsIncorrect() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("text", "searchText")
                        .queryParam("from", "-1")
                        .queryParam("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllByUserId_SizeIsIncorrect() throws Exception {
        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("from", "0")
                        .queryParam("size", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllByUserIdIncorrectFrom() throws Exception {
        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("from", "-1")
                        .queryParam("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateComment_WithoutUser() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(mockCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateComment_WithoutText() throws Exception {
        CommentDto commentDto = CommentDto.builder().id(1L).text("").authorName("Author")
                .created(LocalDateTime.now()).build();

        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isBadRequest());
    }
}
