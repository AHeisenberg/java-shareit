package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    Collection<Booking> findAllByBookerIdAndEndIsBeforeAndStartIsAfterOrderByStartDesc(Long bookerId,
                                                                                       LocalDateTime end,
                                                                                       LocalDateTime start);

    Collection<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    Collection<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    Collection<Booking> findAllByItemOwnerIdAndEndIsBeforeAndStartIsAfterOrderByStartDesc(Long bookerId,
                                                                                          LocalDateTime end,
                                                                                          LocalDateTime start);

    Collection<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    Collection<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    Optional<Booking> findFirstByItemOwnerIdAndStatusOrderByEnd(Long userId, BookingStatus status);

    Optional<Booking> findFirstByItemOwnerIdAndStatusOrderByEndDesc(Long userId, BookingStatus status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusAndStartBefore(Long userId, Long itemId,
                                                                          BookingStatus status, LocalDateTime now);
}
