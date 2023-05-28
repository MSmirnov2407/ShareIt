package ru.practicum.shareit.dataJpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class RequestJpaRepositoryTest {

    public User user;
    public User user2;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;


    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private RequestJpaRepository requestJpaRepository;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("mail@mail.mail");
        user.setName("myName");

        user2 = new User();
        user2.setEmail("2mail@mail.mail");
        user2.setName("2myName");

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("request1");

        itemRequest2 = new ItemRequest();
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("request Description 2");

        userJpaRepository.save(user); //сохранили пользователей в БД
        userJpaRepository.save(user2);

        itemRequest.setRequestor(user); //присвоили запросам их создателей
        itemRequest2.setRequestor(user2);

        requestJpaRepository.save(itemRequest); //сохранили запросы в БД
        requestJpaRepository.save(itemRequest2);
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll(); //Очистили БД
        requestJpaRepository.deleteAll();
    }

    @Test
    void findById() {
        ItemRequest request = requestJpaRepository.findById(1);
    }

    @Test
    void findAllByRequestor_Id() {
        List<ItemRequest> requestList = requestJpaRepository.findAllByRequestor_Id(user.getId(), Sort.by("created").descending());

        assertThat(requestList.size(), equalTo(1)); //один элемент в списке
        assertThat(requestList.get(0).getDescription(), equalTo(itemRequest.getDescription())); //один элемент в списке
    }

    @Test
    void findAllByRequestor_IdNot() {
        PageRequest page = PageRequest.of(0, 1, Sort.by("created").descending()); //параметризируем переменную для пагинации

        List<ItemRequest> requestList = requestJpaRepository.findAllByRequestor_IdNot(user.getId(), page);

        assertThat(requestList.size(), equalTo(1)); //один элемент в списке
        assertThat(requestList.get(0).getDescription(), equalTo(itemRequest2.getDescription())); //один элемент в списке
    }
}