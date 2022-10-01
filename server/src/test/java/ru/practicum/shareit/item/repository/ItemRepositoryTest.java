package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    private static final int PAGE = 0;
    private static final int SIZE = 20;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User mockUserFirst = User.builder().id(1L).name("FirstUser").email("FirstUser@host.com").build();
    private final User mockUserSecond = User.builder().id(2L).name("SecondUser").email("SecondUser@host.com").build();

    private final Item mockItem1 = Item.builder().id(1L).name("Item")
            .description("ItemDescription").available(true).owner(mockUserFirst).build();

    private final Item mockItem2 = Item.builder().name("Item2")
            .description("ItemDescription2").available(true).owner(mockUserSecond).build();

    @Test
    void testFindAllByOwnerId() {
        userRepository.save(mockUserFirst);
        userRepository.save(mockUserSecond);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);

        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Item> items = itemRepository.findAllByOwnerId(1L, page);

        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1).contains(mockItem1);
    }

    @Test
    void testSearch() {
        userRepository.save(mockUserFirst);
        userRepository.save(mockUserSecond);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);

        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<Item> items = itemRepository.search("Description2", page);

        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1).contains(mockItem2);
    }
}
