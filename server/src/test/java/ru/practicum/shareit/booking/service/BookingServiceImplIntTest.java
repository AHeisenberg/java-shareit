package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    private final User mockOwner = User.builder().id(1L).name("Owner").email("Owner@host.com").build();

    private final User mockBooker = User.builder().id(2L).name("Booker").email("Booker@host.com").build();

    private final Item mockItem = Item.builder()
            .id(1L).name("Item").description("ItemDescription").available(true).owner(mockOwner).build();

    private final Booking mockBooking1 = Booking.builder().id(1L)
            .start(LocalDate.now().atStartOfDay().plusDays(1))
            .end(LocalDate.now().atStartOfDay().plusDays(2))
            .item(mockItem).booker(mockBooker).status(BookingStatus.WAITING)
            .build();

    private final Booking mockBooking2 = Booking.builder().id(2L)
            .start(LocalDate.now().atStartOfDay().plusDays(2))
            .end(LocalDate.now().atStartOfDay().plusDays(4))
            .item(mockItem).booker(mockBooker).status(BookingStatus.WAITING)
            .build();

    @Test
    void testFindAllByBookerId() throws Exception {
        userService.createUser(mockOwner);
        userService.createUser(mockBooker);
        itemService.createItem(mockOwner.getId(), mockItem);
        bookingService.createBooking(mockBooker.getId(), mockBooking1);
        bookingService.createBooking(mockBooker.getId(), mockBooking2);

        Collection<Booking> bookings = bookingService.findAllByBookerId(mockBooker.getId(),
                BookingState.WAITING, 0, 20);

        assertThat(bookings, hasSize(2));
        assertThat(bookings.stream().findFirst().isPresent(), is(true));
        assertThat(bookings.stream().findFirst().get().getId(), equalTo(mockBooking2.getId()));
        assertThat(bookings.stream().findFirst().get().getStart(), equalTo(mockBooking2.getStart()));
        assertThat(bookings.stream().findFirst().get().getEnd(), equalTo(mockBooking2.getEnd()));
    }

    @Test
    void testFindAllByBookerId_WrongUser() {
        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.findAllByBookerId(mockBooker.getId(), BookingState.WAITING, 0, 20));

        assertEquals("User with id 2 does not exist", exception.getMessage());
    }
}
