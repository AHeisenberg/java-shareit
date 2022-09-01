package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exc.BookingUnsupportedTypeException;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.UserHasNoRightsException;
import ru.practicum.shareit.exc.ValidationException;

import java.util.Collection;

public interface BookingService {
    Booking createBooking(Long userId, Booking booking) throws ValidationException;

    Booking setApproved(Long userId, Long bookingId, boolean approved) throws ValidationException;

    Booking findBookingById(Long userId, Long bookingId) throws ObjectNotFoundException, UserHasNoRightsException;

    Collection<Booking> findAllByBookerId(Long userId, BookingState state) throws  BookingUnsupportedTypeException;

    Collection<Booking> findAllByOwnerId(Long userId, BookingState state) throws   BookingUnsupportedTypeException;
}

