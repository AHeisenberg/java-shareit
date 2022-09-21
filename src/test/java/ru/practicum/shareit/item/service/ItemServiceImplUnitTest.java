package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
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
class ItemServiceImplUnitTest {

    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;

    private ItemService itemService;

    private MockitoSession session;

    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        itemService = new ItemServiceImpl(bookingRepository, userService, itemRequestService, itemRepository,
                commentRepository);
    }

    @AfterEach
    void finish() {
        session.finishMocking();
    }

    private final User mockUserFirst = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();
    private final User mockUserSecond = User.builder().id(2L).name("SecondUser").email("SecondUser@host.com").build();

    private final ItemRequest mockItemRequest = new ItemRequest(1L, "ItemRequestDesk1", mockUserSecond,
            LocalDateTime.now(), null);

    private final Item mockItem1 = Item.builder().id(1L).name("Item")
            .description("ItemDescription").available(true).owner(mockUserFirst).request(mockItemRequest).build();

    private final Item mockItemWithoutName = Item.builder().id(1L)
            .description("ItemDescription").available(true).owner(mockUserFirst).build();

    private final Item mockItemWithoutDesc = Item.builder().id(1L).name("Item")
            .available(true).owner(mockUserFirst).build();

    private final Item mockItemWithoutAvailable = Item.builder().id(1L).name("Item")
            .description("ItemDescription").owner(mockUserFirst).build();

    private final Item mockUpdatedItem1 = Item.builder().id(1L).name("Item")
            .description("ItemDescription").available(true).owner(mockUserFirst).build();

    private final Item mockItem2 = Item.builder().id(1L).name("Item2")
            .description("ItemDescription2").available(true).owner(mockUserSecond).build();


    private final Booking mockBooking = Booking.builder()
            .id(1L).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(2))
            .item(mockItem1).booker(mockUserSecond).status(BookingStatus.APPROVED).build();

    private final Comment mockComment = Comment.builder()
            .id(1L).text("Comment").item(mockItem1).author(mockUserSecond).created(LocalDateTime.now()).build();


    @Test
    void testCreateItem() throws ValidationException {
        Mockito.when(userService.findUserById(anyLong())).thenReturn(mockUserFirst);
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(mockItem1);

        Item item = itemService.createItem(1L, mockItem1);

        Mockito.verify(itemRepository, Mockito.times(1)).save(mockItem1);

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(mockItem1.getName()));
        assertThat(item.getDescription(), equalTo(mockItem1.getDescription()));
        assertThat(item.getAvailable(), equalTo(mockItem1.getAvailable()));
        assertThat(item.getOwner(), equalTo(mockItem1.getOwner()));
    }

    @Test
    void testCreateItem_FailValidationItemWithoutAvailable() throws ObjectNotFoundException {
        Mockito.when(userService.findUserById(anyLong())).thenReturn(mockUserFirst);

        Exception exception3 = assertThrows(ValidationException.class, () ->
                itemService.createItem(mockUserFirst.getId(), mockItemWithoutAvailable));

        assertEquals("Available field is not filled in", exception3.getMessage());
    }

    @Test
    void testCreateItem_FailValidationItemWithoutName() throws ObjectNotFoundException {
        Mockito.when(userService.findUserById(anyLong())).thenReturn(mockUserFirst);

        Exception exception = assertThrows(ValidationException.class, () ->
                itemService.createItem(mockUserFirst.getId(), mockItemWithoutName));

        assertEquals("Name field is not filled in", exception.getMessage());
    }

    @Test
    void testCreateItem_FailValidationItemWithoutDesc() throws ObjectNotFoundException {
        Mockito.when(userService.findUserById(anyLong())).thenReturn(mockUserFirst);

        Exception exception2 = assertThrows(ValidationException.class, () ->
                itemService.createItem(mockUserFirst.getId(), mockItemWithoutDesc));

        assertEquals("Description field is not filled in", exception2.getMessage());
    }

    @Test
    void testFindItemById() throws ObjectNotFoundException {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(mockItem1));

        Item item = itemService.findItemById(1L, 1L);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);

        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getName(), equalTo(mockItem1.getName()));
        assertThat(item.getDescription(), equalTo(mockItem1.getDescription()));
        assertThat(item.getAvailable(), equalTo(mockItem1.getAvailable()));
        assertThat(item.getOwner(), equalTo(mockItem1.getOwner()));
        assertThat(item.getRequest(), equalTo(mockItemRequest));
        assertThat(item.getComments(), nullValue());
    }

    @Test
    void tesFindItem_WrongId() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                itemService.findItemById(1L, 1L));

        assertEquals("Item with id 1 does not exist", exception.getMessage());
    }

    @Test
    void testFindAllByUserId() throws ObjectNotFoundException {
        Mockito.when(itemRepository.findAllByOwnerId(anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockItem1, mockItem2));

        Collection<Item> items = itemService.findAllByUserId(1L, 0, 20);

        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByOwnerId(anyLong(), Mockito.any(Pageable.class));

        assertThat(items, hasSize(2));
        assertThat(items, equalTo(List.of(mockItem1, mockItem2)));
    }

    @Test
    void testUpdateItem() throws ObjectNotFoundException {
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(mockUpdatedItem1);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(mockItem1));

        mockItem1.setName("Item1Update");

        Item item = itemService.updateItem(1L, mockItem1.getId(), mockItem1);

        Mockito.verify(itemRepository, Mockito.times(1)).save(mockItem1);

        assertThat(item.getId(), equalTo(mockUpdatedItem1.getId()));
        assertThat(item.getName(), equalTo(mockUpdatedItem1.getName()));
    }

    @Test
    void testUpdateItem_WrongUser() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(mockItem1));

        mockItem1.setName("Item1Update");

        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                itemService.updateItem(2L, mockItem1.getId(), mockItem1));

        assertEquals("Passed on to the wrong owner of an item", exception.getMessage());
    }

    @Test
    void testDeleteItem() throws ObjectNotFoundException {
        Mockito.when(itemRepository.existsById(anyLong())).thenReturn(true);

        itemService.deleteItem(1L, 1L);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testSearchItemByText() {
        Mockito.when(itemRepository.search(Mockito.any(String.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockItem2));

        Collection<Item> items = itemService.searchItemByText("Desc2", 0, 20);

        Mockito.verify(itemRepository, Mockito.times(1))
                .search(Mockito.any(String.class), Mockito.any(Pageable.class));

        assertThat(items, hasSize(1));
        assertThat(items, equalTo(List.of(mockItem2)));
    }

    @Test
    void testCreateComment() throws ValidationException {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(mockUserSecond);
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(mockItem1));
        Mockito.when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(anyLong(),
                        anyLong(), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockBooking));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(mockComment);

        Comment comment = itemService.addComment(2L, mockItem1.getId(), mockComment);

        Mockito.verify(commentRepository, Mockito.times(1)).save(mockComment);

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(mockComment.getText()));
        assertThat(comment.getItem(), equalTo(mockComment.getItem()));
        assertThat(comment.getAuthor(), equalTo(mockComment.getAuthor()));
    }

    @Test
    void testCreateComment_ValidationFailed() throws ObjectNotFoundException {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(mockUserSecond);
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(mockItem1));
        Mockito.when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(anyLong(),
                        anyLong(), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Exception exception1 = assertThrows(ValidationException.class, () ->
                itemService.addComment(mockUserFirst.getId(), mockItem1.getId(), mockComment));

        assertEquals("The user with id 1 did not take the item with id 1 on lease", exception1.getMessage());
    }
}
