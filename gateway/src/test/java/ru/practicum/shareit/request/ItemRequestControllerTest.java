package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final ItemRequestDto mockItemRequestDto = ItemRequestDto.builder().id(1L).description("ItemRequestDesk")
            .requestor(1L).created(LocalDateTime.now()).build();

    @Test
    void testCreateItemRequest_WithoutUser() throws Exception {
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(mockItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateItemRequest_WithoutDesc() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).description("")
                .requestor(1L).created(LocalDateTime.now()).build();

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllItemRequest_WithoutUser() throws Exception {
        mockMvc.perform(get("/requests/all")).andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllItemRequest_SizeIsIncorrect() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("from", "0")
                        .queryParam("size", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllItemRequest_FromIsIncorrect() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("from", "-1")
                        .queryParam("size", "10"))
                .andExpect(status().isBadRequest());
    }
}
