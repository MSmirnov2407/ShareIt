package ru.practicum.shareit.dataJpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class UserJpaRepositoryTest {

    public User user;
    public User user2;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("mail@mail.mail");
        user.setName("myName");

        user2 = new User();
        user2.setEmail("2mail@mail.mail");
        user2.setName("2myName");

        userJpaRepository.save(user); //сохранили юзеров в бд
        userJpaRepository.save(user2);
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll(); //очистили БД
    }

    @Test
    void getUsersEmails() {
        List<String> emails = userJpaRepository.getUsersEmails(); //взяли email всех пользователей

        assertThat(emails.size(), equalTo(2)); //два элемента в списке
        assertThat(emails.get(0), equalTo(user2.getEmail())); //элемент имеет заданные значения полей
        assertThat(emails.get(1), equalTo(user.getEmail())); //элемент имеет заданные значения полей
    }
}