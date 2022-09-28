package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.UserHasNoRightsException;
import ru.practicum.shareit.exc.ValidationException;

import java.util.Collection;

public interface BookingService {

    Booking createBooking(long userId, Booking booking) throws ValidationException;

    Booking setApproved(long userId, long bookingId, boolean approved) throws ValidationException;

    Booking findBookingById(long userId, long bookingId) throws ObjectNotFoundException, UserHasNoRightsException;

    Collection<Booking> findAllByBookerId(long userId, BookingState state, int from, int size)
            throws ObjectNotFoundException;

    Collection<Booking> findAllByOwnerId(long userId, BookingState state, int from, int size)
            throws ObjectNotFoundException;
}
