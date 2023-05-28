package ru.practicum.shareit.dataJpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemJpaRepositoryTest {
    public UserDto userDto;
    public User user;
    public User user2;
    public ItemRequest itemRequest;
    public ItemDto itemDto;
    public Item item;
    public Item item2;

    @Autowired
    private ItemJpaRepository itemJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private RequestJpaRepository requestJpaRepository;

    @BeforeEach
    public void setUp() {
        /*создаем тестовые объекты*/
        userDto = new UserDto();
        userDto.setEmail("mail@mail.mail");
        userDto.setName("myName");

        user = new User();
        user.setEmail("mail@mail.mail");
        user.setName("myName");

        user2 = new User();
        user2.setEmail("2mail@mail.mail");
        user2.setName("2myName");

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("request Description");

        itemDto = new ItemDto();
        itemDto.setDescription("item description");
        itemDto.setName("item name");
        itemDto.setIsAvailable(true);
        itemDto.setId(33);
        itemDto.setRequestId(22);

        item = new Item();
        item.setDescription("item description");
        item.setName("item name");
        item.setIsAvailable(true);
        item.setOwner(user);
        item.setRequest(itemRequest);

        item2 = new Item();
        item2.setDescription("item description2");
        item2.setName("item name2");
        item2.setIsAvailable(true);
        item2.setOwner(user);
        item2.setRequest(itemRequest);

        userJpaRepository.save(user);//сохранили пользоватедя в БД
        userJpaRepository.save(user2);//сохранили пользоватедя в БД

        itemRequest.setRequestor(user2);
        requestJpaRepository.save(itemRequest); //сохранили запрос в БД

        itemJpaRepository.save(item); //сохранили item в бд
        itemJpaRepository.save(item2); //сохранили item в бд
    }

    @Test
    void findAllByOwnerId() {
        PageRequest page = PageRequest.of(0, 5); //параметризируем переменную для пагинации
        User owner = userJpaRepository.findAll().get(0); //берем юзера из БД

        List<Item> itemList = itemJpaRepository.findAllByOwnerId(owner.getId(), page); //запросили вещи пользователя

        assertThat(itemList.size(), equalTo(2)); //два элемента в списке
        assertThat(itemList.get(0).getDescription(), equalTo(item.getDescription())); //элемент имеет заданные значения полей
        assertThat(itemList.get(0).getName(), equalTo(item.getName())); //элемент имеет заданные значения полей
        assertTrue(itemList.get(0).getIsAvailable()); //элемент имеет заданные значения полей
    }

    @Test
    void searchItems() {
        PageRequest page = PageRequest.of(0, 5); //параметризируем переменную для пагинации
        User owner = userJpaRepository.findAll().get(0); //берем юзера из БД
        String searchRequest = "description2";

        List<Item> itemList = itemJpaRepository.searchItems(searchRequest, page); //запросили вещи по поисковому запросу
        assertThat(itemList.size(), equalTo(1)); //один элемент в списке
        assertTrue(itemList.get(0).getDescription().contains(searchRequest)); //элемент имеет заданные значения полей
    }

    @Test
    void findByRequest_IdIn() {
        ItemRequest request = requestJpaRepository.findAll().get(0); //единственный запрос из БД
        List<Integer> requestIds = Arrays.asList(10, 20, 30, request.getId()); //сформировали списко id запросов

        List<Item> itemList = itemJpaRepository.findByRequest_IdIn(requestIds); //запросили вещи по всем requestId
        assertThat(itemList.size(), equalTo(2)); //два элемента в списке
        assertThat(itemList.get(0).getRequest().getDescription(), equalTo(itemRequest.getDescription())); //элемент имеет заданные значения полей
        assertThat(itemList.get(1).getRequest().getDescription(), equalTo(itemRequest.getDescription())); //элемент имеет заданные значения полей
    }

    @AfterEach
    public void afterTest() {
        itemJpaRepository.deleteAll();//очистили бд
        userJpaRepository.deleteAll();//очистили бд
        requestJpaRepository.deleteAll();//очистили бд
    }
}
