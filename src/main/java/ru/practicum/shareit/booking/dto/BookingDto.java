package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus bookingStatus;

    @Data
    @Builder
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private boolean available;
        private String request;
    }

    @Data
    @Builder
    public static class User {
        private Long id;
        private String name;
        private String email;
    }
}
