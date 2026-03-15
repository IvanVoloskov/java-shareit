package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private Item item1;
    private Item item2;
    private Item unavailableItem;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        em.persist(owner);

        item1 = new Item();
        item1.setName("Drill");
        item1.setDescription("Powerful electric drill");
        item1.setAvailable(true);
        item1.setOwner(owner);
        em.persist(item1);

        item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer for construction");
        item2.setAvailable(true);
        item2.setOwner(owner);
        em.persist(item2);

        unavailableItem = new Item();
        unavailableItem.setName("Saw");
        unavailableItem.setDescription("Sharp saw for wood");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(owner);
        em.persist(unavailableItem);
    }

    @Test
    void findAllByOwnerId_ShouldReturnAllOwnerItems() {
        List<Item> items = itemRepository.findAllByOwnerId(owner.getId());

        assertEquals(3, items.size());
    }

    @Test
    void findAllByOwnerId_ShouldReturnEmpty_WhenNoItems() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@mail.com");
        em.persist(newUser);

        List<Item> items = itemRepository.findAllByOwnerId(newUser.getId());

        assertTrue(items.isEmpty());
    }

    @Test
    void searchAvailableItemsByDescriptionOrName_ShouldFindByName() {
        List<Item> items = itemRepository.searchAvailableItemsByDescriptionOrName("drill");

        assertEquals(1, items.size());
        assertEquals("Drill", items.get(0).getName());
    }

    @Test
    void searchAvailableItemsByDescriptionOrName_ShouldFindByDescription() {
        List<Item> items = itemRepository.searchAvailableItemsByDescriptionOrName("electric");

        assertEquals(1, items.size());
        assertEquals("Drill", items.get(0).getName());
    }

    @Test
    void searchAvailableItemsByDescriptionOrName_ShouldBeCaseInsensitive() {
        List<Item> items = itemRepository.searchAvailableItemsByDescriptionOrName("DRILL");

        assertEquals(1, items.size());
        assertEquals("Drill", items.get(0).getName());
    }

    @Test
    void searchAvailableItemsByDescriptionOrName_ShouldNotReturnUnavailableItems() {
        List<Item> items = itemRepository.searchAvailableItemsByDescriptionOrName("saw");

        assertTrue(items.isEmpty());
    }

    @Test
    void searchAvailableItemsByDescriptionOrName_ShouldReturnEmpty_WhenNoMatches() {
        List<Item> items = itemRepository.searchAvailableItemsByDescriptionOrName("nonexistent");

        assertTrue(items.isEmpty());
    }
}