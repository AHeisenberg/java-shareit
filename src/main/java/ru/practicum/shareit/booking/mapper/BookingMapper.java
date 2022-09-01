package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Service
public class BookingMapper {

    public CreatedBookingDto toCreatedBookingDto(Booking booking) {
        return new CreatedBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId()
        );
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                toItem(booking.getItem()),
                toUser(booking.getBooker()),
                booking.getStatus()
        );
    }

    public Booking toBooking(CreatedBookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                new Item(bookingDto.getItemId(), null, null, null, null, null, null, null, null),
                null,
                null
        );
    }

    private BookingDto.Item toItem(Item item) {
        return new BookingDto.Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    private BookingDto.User toUser(User user) {
        return new BookingDto.User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}

