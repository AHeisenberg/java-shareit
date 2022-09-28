package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId and current_timestamp > b.start" +
            " and current_timestamp < b.end")
    List<Booking> findForBookerCurrent(long bookerId, Pageable page);//

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :id and b.start > current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByBookerAndFutureState(long id, Pageable page);//

    List<Booking> findAllByBookerId(long userId, Pageable page);//

    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBefore(long bookerId, LocalDateTime end, LocalDateTime start,
                                                                 Pageable page);//delete

    List<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime start, Pageable page);//delete

    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status, Pageable page);//

    List<Booking> findAllByItemOwnerId(long userId, Pageable page);//

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStatus(long bookerId, BookingStatus status, Pageable page);

    Optional<Booking> findFirstByItemIdAndStatusOrderByEnd(long itemId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStatusOrderByEndDesc(long itemId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long bookerId, LocalDateTime start, Pageable page);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusAndStartBefore(long userId, long itemId,
                                                                          BookingStatus status, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(long bookerId,
                                                                    LocalDateTime end,
                                                                    LocalDateTime start,
                                                                    Pageable page);
}
