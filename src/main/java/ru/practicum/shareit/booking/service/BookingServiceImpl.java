package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.UserHasNoRightsException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private static final String ERROR_MESSAGE_DATE = "The start or end date of the booking is incorrect";

    @Override
    public Booking createBooking(long userId, Booking booking) throws ValidationException {
        userService.checkUserId(userId);

        Item item = itemService.findItemById(userId, booking.getItem().getId());

        if (!item.getAvailable()) {
            throw new ValidationException("unavailable item", "CreateBooking");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new UserHasNoRightsException(ERROR_MESSAGE_DATE, "CreateBooking");
        }
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now())
                || booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException(ERROR_MESSAGE_DATE, "CreateBooking");
        }

        booking.setBooker(new User(userId, null, null));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        log.info("Created a booking with id {}", booking.getId());
        return bookingRepository.save(booking);
    }

    @Override
    public Booking setApproved(long userId, long bookingId, boolean approved) throws ValidationException {
        Booking booking = findBookingById(userId, bookingId);

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException(String.format(
                    "Reservation with id %d is not pending confirmation", bookingId), "SetStatus");
        }

        if (booking.getItem().getOwner().getId() != userId) {
            throw new UserHasNoRightsException(String.format(
                    "User with id %d has no right to change status", userId), "SetApproved");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking findBookingById(long userId, long bookingId)
            throws ObjectNotFoundException, UserHasNoRightsException {
        userService.checkUserId(userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException(
                String.format("Booking with id %d does not exist", bookingId), "findBookingById"));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new UserHasNoRightsException(String.format("User with id %d has no right", userId), "GetBookingById");
        }

        return booking;
    }

    @Override
    public Collection<Booking> findAllByBookerId(long userId, BookingState state) throws ObjectNotFoundException {
        userService.checkUserId(userId);

        Collection<Booking> result = null;

        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                result = bookingRepository.findForBookerCurrent(userId);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerAndFutureState(userId);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return result;
    }


    @Override
    public Collection<Booking> findAllByOwnerId(long userId, BookingState state) throws ObjectNotFoundException {
        userService.checkUserId(userId);

        Collection<Booking> result = null;

        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;

            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return result;
    }
}