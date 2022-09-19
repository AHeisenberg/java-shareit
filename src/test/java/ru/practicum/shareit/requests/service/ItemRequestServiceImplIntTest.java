package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exc.ObjectNotFoundException;
import ru.practicum.shareit.exc.ValidationException;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceImplIntTest {
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    private final User mockUser = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();

    private final ItemRequest mockItemRequest = ItemRequest.builder().id(1L).description("ItemRequestDescription")
            .created(LocalDate.now().atStartOfDay()).build();

    @Test
    void testFindItemRequestById() throws ValidationException {
        userService.createUser(mockUser);
        itemRequestService.createItemRequest(mockUser.getId(), mockItemRequest);

        ItemRequest itemRequest = itemRequestService.findItemRequestById(mockUser.getId(), mockItemRequest.getId());

        assertThat(itemRequest.getId(), equalTo(mockItemRequest.getId()));
        assertThat(itemRequest.getDescription(), equalTo(mockItemRequest.getDescription()));
    }


    @Test
    void testFindItemRequest_WrongId() {
        userService.createUser(mockUser);

        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                itemRequestService.findItemRequestById(mockUser.getId(), mockItemRequest.getId()));

        assertEquals("No request for an item with id 1", exception.getMessage());
    }
}
