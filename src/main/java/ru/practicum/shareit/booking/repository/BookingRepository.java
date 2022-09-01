package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    Collection<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBefore(long bookerId, LocalDateTime end, LocalDateTime start);

    List<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status);

    Collection<Booking> findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(long bookerId,
                                                                          LocalDateTime end,
                                                                          LocalDateTime start);

    Collection<Booking> findAllByItemOwnerIdAndEndIsBefore(long bookerId, LocalDateTime end);

    Collection<Booking> findAllByItemOwnerIdAndStatus(long bookerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStatusOrderByEnd(long itemId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStatusOrderByEndDesc(long itemId, BookingStatus status);

    Collection<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusAndStartBefore(long userId, long itemId,
                                                                          BookingStatus status, LocalDateTime now);
}
