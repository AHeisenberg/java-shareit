package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    private static final int PAGE = 0;
    private static final int SIZE = 20;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private final User firstUser = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();
    private  final User secondUser = User.builder().id(2L).name("SecondUser").email("SecondUser@host.com").build();
    private final ItemRequest mockItemRequest1 = ItemRequest.builder().id(1L).description("ItemRequestDescription1")
            .requestor(firstUser).created(LocalDateTime.now()).build();
    private final ItemRequest mockItemRequest2 = ItemRequest.builder().id(2L).description("ItemRequestDescription2")
            .requestor(secondUser).created(LocalDateTime.now().plusDays(1)).build();


    @Test
    void testFindAllByRequestorIdOrderByCreatedDesc() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRequestRepository.save(mockItemRequest1);
        itemRequestRepository.save(mockItemRequest2);

        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1L);

        assertThat(itemRequests).isNotEmpty();
        assertThat(itemRequests).hasSize(1).contains(mockItemRequest1);
    }

    @Test
    void testFindAllByRequestorIdNotOrderByCreatedDesc() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRequestRepository.save(mockItemRequest1);
        itemRequestRepository.save(mockItemRequest2);

        Sort sortById = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(1L,page);

        assertThat(itemRequests).isNotEmpty();
        assertThat(itemRequests).hasSize(1).contains(mockItemRequest2);
    }
}