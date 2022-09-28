package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.exc.InvalidParamException;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplUnitTest {
    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemRequestService itemRequestService;

    private MockitoSession mockitoSession;

    @BeforeEach
    void setUp() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        itemRequestService = new ItemRequestServiceImpl(userService, itemRequestRepository);
    }

    @AfterEach
    void finish() {
        mockitoSession.finishMocking();
    }

    private final User firstUser = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();
    private final User secondUser = User.builder().id(2L).name("SecondUser").email("SecondUser@host.com").build();
    private final ItemRequest mockItemRequest1 = ItemRequest.builder().id(1L).description("ItemRequestDescription1")
            .requestor(firstUser).created(LocalDateTime.now()).build();
    private final ItemRequest mockItemRequest2 = ItemRequest.builder().id(2L).description("ItemRequestDescription2")
            .requestor(secondUser).created(LocalDateTime.now().plusDays(1)).build();
    private final ItemRequest mockItemRequestWithoutDescAndUSer = ItemRequest.builder().id(2L)
            .created(LocalDateTime.now().plusDays(1)).build();
    private final ItemRequest mockItemRequest3 = ItemRequest.builder().id(3L).description("ItemRequestDescription3")
            .requestor(firstUser).created(LocalDateTime.now()).build();


    @Test
    void testCreateItemRequest() throws ValidationException {
        Mockito.when(userService.findUserById(anyLong())).thenReturn(secondUser);
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(mockItemRequest1);

        ItemRequest itemRequest = itemRequestService.createItemRequest(2L, mockItemRequest1);

        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(mockItemRequest1);

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(mockItemRequest1.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(mockItemRequest1.getCreated()));
    }

    @Test
    void testCreateItemRequest_WithoutDesc() throws ObjectNotFoundException {
        Mockito.when(userService.findUserById(anyLong())).thenReturn(secondUser);

        Exception exception = assertThrows(ValidationException.class, () ->
                itemRequestService.createItemRequest(firstUser.getId(), mockItemRequestWithoutDescAndUSer));

        assertEquals("The description field is empty", exception.getMessage());
    }

    @Test
    void testFindItemRequestsByUser() throws ObjectNotFoundException {
        Mockito.when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(mockItemRequest1, mockItemRequest3));

        Collection<ItemRequest> itemRequests = itemRequestService.findItemRequestsByUser(1L);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorIdOrderByCreatedDesc(1L);

        assertThat(itemRequests, hasSize(2));
        assertThat(itemRequests, equalTo(List.of(mockItemRequest1, mockItemRequest3)));
    }

    @Test
    void testFindAllItemRequest() throws ObjectNotFoundException, InvalidParamException {
        Mockito.when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockItemRequest1, mockItemRequest2, mockItemRequest3));

        Collection<ItemRequest> itemRequests = itemRequestService.findAllItemRequest(1L, 0, 20);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorIdNotOrderByCreatedDesc(anyLong(), Mockito.any(Pageable.class));

        assertThat(itemRequests, hasSize(3));
        assertThat(itemRequests, equalTo(List.of(mockItemRequest1, mockItemRequest2, mockItemRequest3)));
    }

    @Test
    void testFindItemRequestById() throws ObjectNotFoundException {
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(mockItemRequest1));

        ItemRequest itemRequests = itemRequestService.findItemRequestById(1L, 1L);

        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(1L);

        assertThat(itemRequests.getId(), equalTo(1L));
        assertThat(itemRequests.getDescription(), equalTo(mockItemRequest1.getDescription()));
        assertThat(itemRequests.getCreated(), equalTo(mockItemRequest1.getCreated()));
    }

    @Test
    void testFindItemRequest_WrongId() {
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                itemRequestService.findItemRequestById(1L, 1L));

        assertEquals("No request for an item with id 1", exception.getMessage());
    }

    @Test
    void testCheckItemRequestExistsById() throws ObjectNotFoundException {
        Mockito.when(itemRequestRepository.existsById(anyLong())).thenReturn(true);

        itemRequestService.checkItemRequestExistsById(1L);

        Mockito.verify(itemRequestRepository, Mockito.times(1)).existsById(1L);
    }

    @Test
    void testCheckItemRequestNotExistsById() {
        Mockito.when(itemRequestRepository.existsById(anyLong())).thenReturn(false);

        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                itemRequestService.checkItemRequestExistsById(1L));

        assertEquals("No request for an item with id 1", exception.getMessage());
    }
}

