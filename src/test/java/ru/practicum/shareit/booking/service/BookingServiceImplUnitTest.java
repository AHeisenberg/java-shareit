package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    @Mock
    private BookingRepository bookingRepository;

    private BookingService bookingService;

    private MockitoSession mockitoSession;

    @BeforeEach
    void setUp() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        bookingService = new BookingServiceImpl(userService, itemService, bookingRepository);
    }

    @AfterEach
    void finish() {
        mockitoSession.finishMocking();
    }

    private final User mockUserFirst = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();
    private final User mockUserSecond = User.builder().id(2L).name("SecondUser").email("SecondUser@host.com").build();
    private final Item mockItem1 = Item.builder().id(1L).name("Item")
            .description("ItemDescription").available(true).owner(mockUserFirst).build();

    private final Item mockItemUnAvailable = Item.builder().id(1L).name("Item")
            .description("ItemDescription").available(false).owner(mockUserFirst).build();

    private final Booking mockBooking1 = Booking.builder().id(1L)
            .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
            .item(mockItem1).booker(mockUserSecond).status(BookingStatus.WAITING).build();
    private final Booking mockBookingUnAvailable = Booking.builder()
            .id(1L).start(LocalDateTime.now().plusDays(3)).end(LocalDateTime.now().plusDays(4))
            .item(mockItemUnAvailable).booker(mockUserSecond).status(BookingStatus.WAITING).build();

    private final Booking mockBookingEndFromLast = Booking.builder()
            .id(1L).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().minusDays(6))
            .item(mockItem1).booker(mockUserSecond).status(BookingStatus.WAITING).build();

    private final Booking mockBookingStartFromLast = Booking.builder()
            .id(1L).start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(6))
            .item(mockItem1).booker(mockUserSecond).status(BookingStatus.WAITING).build();
    private final Booking mockBookingStartAfterEnd = Booking.builder()
            .id(1L).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(2))
            .item(mockItem1).booker(mockUserSecond).status(BookingStatus.WAITING).build();
    private final Booking mockBookingWrongUser = Booking.builder()
            .id(1L).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(10))
            .item(mockItem1).booker(mockUserFirst).status(BookingStatus.WAITING).build();
    private final Booking mockBookingApproved1 = Booking.builder()
            .id(1L).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(10))
            .item(mockItem1).booker(mockUserSecond).status(BookingStatus.APPROVED).build();
    private final Booking mockBookingRejected1 = Booking.builder()
            .id(1L).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(10))
            .item(mockItem1).booker(mockUserSecond).status(BookingStatus.REJECTED).build();

    private final Booking mockBooking2 = Booking.builder()
            .id(1L).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(10))
            .item(mockItem1).booker(mockUserSecond).status(BookingStatus.WAITING).build();

    private final Booking mockBooking3 = Booking.builder()
            .id(5L).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
            .item(mockItem1).booker(mockUserSecond).status(BookingStatus.WAITING).build();

    @Test
    void testCreateBooking() throws ValidationException {
        Mockito.when(itemService.findItemById(anyLong(), anyLong())).thenReturn(mockItem1);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(mockBooking1);

        Booking booking = bookingService.createBooking(2L, mockBooking1);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(mockBooking1);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(mockBooking1.getStart()));
        assertThat(booking.getEnd(), equalTo(mockBooking1.getEnd()));
    }

    @Test
    void testCreateBooking_FailedValidationBooking() throws ObjectNotFoundException {
        Mockito.when(itemService.findItemById(anyLong(), anyLong())).thenReturn(mockItem1);

        Exception exception1 = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2L, mockBookingStartFromLast));

        assertEquals("The start or end date of the booking is incorrect", exception1.getMessage());

        Exception exception2 = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2L, mockBookingEndFromLast));

        assertEquals("The start or end date of the booking is incorrect", exception2.getMessage());

        Exception exception3 = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2L, mockBookingStartAfterEnd));

        assertEquals("The start or end date of the booking is incorrect", exception3.getMessage());

        Exception exception4 = assertThrows(UserHasNoRightsException.class, () ->
                bookingService.createBooking(1L, mockBookingWrongUser));

        assertEquals("The start or end date of the booking is incorrect", exception4.getMessage());
    }

    @Test
    void testCreateBooking_FailedValidationItem() throws ObjectNotFoundException {
        Mockito.when(itemService.findItemById(anyLong(), anyLong())).thenReturn(mockItemUnAvailable);

        Exception exception1 = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2L, mockBookingUnAvailable));

        assertEquals("unavailable item", exception1.getMessage());
    }

    @Test
    void testSetApproved() throws ValidationException {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(mockBooking1));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(mockBookingApproved1);

        Booking booking = bookingService.setApproved(1L, mockBooking1.getId(), true);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(mockBooking1);

        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void testSetApproved_StatusNotWaiting() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(mockBookingApproved1));

        Exception exception = assertThrows(ValidationException.class, () ->
                bookingService.setApproved(1L, mockBookingApproved1.getId(), true));

        assertEquals("Reservation with id 1 is not pending confirmation", exception.getMessage());
    }

    @Test
    void testSetApproved_WrongUser() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(mockBooking1));

        Exception exception = assertThrows(UserHasNoRightsException.class, () ->
                bookingService.setApproved(2L, mockBooking1.getId(), true));

        assertEquals("User with id 2 has no right to change status", exception.getMessage());
    }

    @Test
    void testSetNotApproved() throws ValidationException {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(mockBooking2));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(mockBookingRejected1);

        Booking booking = bookingService.setApproved(1L, mockBooking2.getId(), false);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(mockBooking2);

        assertThat(booking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void testFindBookingById() throws UserHasNoRightsException, ObjectNotFoundException {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(mockBooking1));

        Booking booking = bookingService.findBookingById(1L, 1L);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1L);

        assertThat(booking.getId(), equalTo(mockBooking1.getId()));
        assertThat(booking.getStatus(), equalTo(mockBooking1.getStatus()));
        assertThat(booking.getStart(), equalTo(mockBooking1.getStart()));
        assertThat(booking.getEnd(), equalTo(mockBooking1.getEnd()));
        assertThat(booking.getItem(), equalTo(mockBooking1.getItem()));
    }

    @Test
    void testFindBooking_WrongId() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.findBookingById(1L, 1L));

        assertEquals("Booking with id 1 does not exist", exception.getMessage());
    }

    @Test
    void testFindBookingById_WrongUser() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(mockBooking1));

        Exception exception = assertThrows(UserHasNoRightsException.class, () ->
                bookingService.findBookingById(3L, 1L));

        assertEquals("User with id 3 has no right", exception.getMessage());
    }

    @Test
    void testFindAllByBookerId_StateAll() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByBookerId(anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1L, BookingState.ALL, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerId(anyLong(), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(3));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2, mockBooking3)));
    }

    @Test
    void testFindAllByBookerId_StateCurrent() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findForBookerCurrent(anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1L,
                BookingState.CURRENT, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findForBookerCurrent(anyLong(), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking2, mockBooking3)));
    }

    @Test
    void testFindAllByBookerId_StatePast() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1L, BookingState.PAST, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndEndIsBefore(anyLong(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2)));
    }

    @Test
    void testFindAllByBookerId_StateFuture() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByBookerAndFutureState(anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1L, BookingState.FUTURE, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerAndFutureState(anyLong(),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking3)));
    }

    @Test
    void testFindAllByBookerId_StateWaiting() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(anyLong(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1L,
                BookingState.WAITING, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(anyLong(), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking3)));
    }

    @Test
    void testFindAllByBookerId_StateRejected() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(anyLong(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1L,
                BookingState.REJECTED, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(anyLong(), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking2)));
    }

    @Test
    void testFindAllByOwnerId_StateAll() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByItemOwnerId(anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1L, BookingState.ALL, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerId(anyLong(), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(3));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2, mockBooking3)));
    }

    @Test
    void testFindAllByOwnerId_StateCurrent() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1L, BookingState.CURRENT, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking2, mockBooking3)));
    }

    @Test
    void testGetAllByOwnerId_StatePast() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1L, BookingState.PAST, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndEndIsBefore(anyLong(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2)));
    }

    @Test
    void testGetAllByOwnerId_StateFuture() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1L, BookingState.FUTURE, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStartIsAfter(anyLong(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking3)));
    }

    @Test
    void testGetAllByOwnerId_StateWaiting() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1L, BookingState.WAITING, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(anyLong(), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking3)));
    }

    @Test
    void testGetAllByOwnerId_StateRejected() throws ObjectNotFoundException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1L, BookingState.REJECTED, 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(anyLong(), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking2)));
    }
}
