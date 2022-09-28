package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingDto.Item item;
    private BookingDto.User booker;
    private BookingStatus status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private boolean available;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class User {
        private Long id;
        private String name;
        private String email;
    }
}
