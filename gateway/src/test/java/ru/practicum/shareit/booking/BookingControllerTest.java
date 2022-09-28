package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final BookItemRequestDto bookItemRequestDto = BookItemRequestDto.builder()
            .id(1L).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(6))
            .itemId(1L).build();

    @Test
    void testCreateBooking_WithoutUser() throws Exception {
        mockMvc.perform(get("/bookings")
                        .content(objectMapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllByBookerId_StateIsUnsupported() throws Exception {
        mockMvc.perform(get("/bookings")
                        .content(objectMapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1)
                        .queryParam("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));
    }

    @Test
    void testFindAllByOwnerId_StateIsUnsupported() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .content(objectMapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1)
                        .queryParam("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));
    }
}
