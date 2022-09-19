package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exc.InvalidParamException;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.UserHasNoRightsException;
import ru.practicum.shareit.exc.ValidationException;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private static final String FROM = "0";
    private static final String SIZE = "20";
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public CreatedBookingDto createItem(@RequestHeader(HEADER_USER_ID) long userId,
                                        @Valid @RequestBody CreatedBookingDto bookingDto)
            throws ValidationException {
        Booking booking = bookingService.createBooking(userId, bookingMapper.toBooking(bookingDto));
        return bookingMapper.toCreatedBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApproved(@RequestHeader(HEADER_USER_ID) long userId,
                                  @PathVariable long bookingId, @RequestParam boolean approved)
            throws ValidationException {
        return bookingMapper.toBookingDto((bookingService.setApproved(userId, bookingId, approved)));
    }

    @GetMapping("/{bookingId}")
    public BookingDto findItemById(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long bookingId)
            throws ObjectNotFoundException, UserHasNoRightsException {
        return bookingMapper.toBookingDto(bookingService.findBookingById(userId, bookingId));
    }

    @GetMapping
    public Collection<BookingDto> findAllByBookerId(@RequestHeader(HEADER_USER_ID) long userId,
                                                    @RequestParam(defaultValue = "ALL") BookingState state,
                                                    @RequestParam(defaultValue = FROM) int from,
                                                    @RequestParam(defaultValue = SIZE) int size)
            throws ObjectNotFoundException, InvalidParamException {
        return bookingService.findAllByBookerId(userId, state, from, size)
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllByOwnerId(@RequestHeader(HEADER_USER_ID) long userId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state,
                                                   @RequestParam(defaultValue = FROM) int from,
                                                   @RequestParam(defaultValue = SIZE) int size)
            throws ObjectNotFoundException, InvalidParamException {
        return bookingService.findAllByOwnerId(userId, state, from, size)
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
