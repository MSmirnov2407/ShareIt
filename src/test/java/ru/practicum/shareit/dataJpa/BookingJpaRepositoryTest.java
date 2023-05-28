package ru.practicum.shareit.dataJpa;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class BookingJpaRepositoryTest {

    private User user;
    private User user2;
    private Item item;
    private Item item2;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ItemJpaRepository itemJpaRepository;
    @Autowired
    private BookingJpaRepository bookingJpaRepository;
    private Booking booking;
    private Booking booking2;
    private Booking booking3;

    @BeforeEach
    public void setUp() {
        /*создаем тестовые объекты*/
        user = new User();
        user.setEmail("mail@mail.mail");
        user.setName("myName");

        user2 = new User();
        user2.setEmail("2mail@mail.mail");
        user2.setName("2myName");

        userJpaRepository.save(user);//сохранили пользоватедя в БД
        userJpaRepository.save(user2);//сохранили пользоватедя в БД

        item = new Item();
        item.setDescription("item description");
        item.setName("item name");
        item.setIsAvailable(true);
        item.setOwner(user);

        item2 = new Item();
        item2.setDescription("item description2");
        item2.setName("item name2");
        item2.setIsAvailable(true);
        item2.setOwner(user2);

        itemJpaRepository.save(item); //сохранили item в бд
        itemJpaRepository.save(item2); //сохранили item в бд

        booking = new Booking();
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusSeconds(1));
        booking.setBooker(user2); //чтобы запрос был НЕ от собственника вещи

        booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusSeconds(2));
        booking2.setBooker(user);

        booking3 = new Booking();
        booking3.setItem(item2);
        booking3.setStart(LocalDateTime.now().plusSeconds(2));
        booking3.setEnd(LocalDateTime.now().plusSeconds(5));
        booking3.setBooker(user);
        booking3.setStatus(Status.REJECTED);

        bookingJpaRepository.save(booking); //сохранили item в бд
        bookingJpaRepository.save(booking2); //сохранили item в бд
        bookingJpaRepository.save(booking3); //сохранили item в бд
    }

    @AfterEach
    public void afterTest() {
        userJpaRepository.deleteAll();//очистили бд
        bookingJpaRepository.deleteAll();//очистили бд
    }

    @Test
    void findByOwner() {
        PageRequest page = PageRequest.of(0, 5); //параметризируем переменную для пагинации

        List<Booking> bookingList = bookingJpaRepository.findByOwner(user.getId(), page);

        assertThat(bookingList.size(), equalTo(1)); //один элемент в списке
        assertThat(bookingList.get(0).getItem().getName(), equalTo(item.getName())); //поля элемента соотв-т ожидаемым
    }

    @Test
    void findByOwnerAndStatus() {
        PageRequest page = PageRequest.of(0, 5); //параметризируем переменную для пагинации

        List<Booking> bookingList = bookingJpaRepository.findByOwnerAndStatus(user2.getId(), Status.REJECTED, page);

        assertThat(bookingList.size(), equalTo(1)); //один элемент в списке
        assertThat(bookingList.get(0).getItem().getName(), equalTo(item2.getName())); //поля элемента соотв-т ожидаемым
        assertThat(bookingList.get(0).getStatus(), equalTo(booking3.getStatus())); //поля элемента соотв-т ожидаемым
    }

    @Test
    @SneakyThrows
    void findPastByOwner() {
        PageRequest page = PageRequest.of(0, 5); //параметризируем переменную для пагинации

        TimeUnit.SECONDS.sleep(3); //временная задержка, чтобы проверка оказалась ПОСЛЕ окончания бронирования
        List<Booking> bookingList = bookingJpaRepository.findPastByOwner(user2.getId(), LocalDateTime.now(), page);

        assertThat(bookingList.size(), equalTo(1)); //один элемент в списке
        assertThat(bookingList.get(0).getItem().getName(), equalTo(item2.getName())); //поля элемента соотв-т ожидаемым
        assertThat(bookingList.get(0).getId(), equalTo(booking2.getId())); //поля элемента соотв-т ожидаемым
    }

    @Test
    @SneakyThrows
    void findFutureByOwner() {
        PageRequest page = PageRequest.of(0, 5); //параметризируем переменную для пагинации

        List<Booking> bookingList = bookingJpaRepository.findFutureByOwner(user2.getId(), LocalDateTime.now(), page);

        assertThat(bookingList.size(), equalTo(1)); //один элемент в списке
        assertThat(bookingList.get(0).getId(), equalTo(booking3.getId())); //поля элемента соотв-т ожидаемым
    }

    @Test
    @SneakyThrows
    void findCurrentByOwner() {
        PageRequest page = PageRequest.of(0, 5); //параметризируем переменную для пагинации

        TimeUnit.SECONDS.sleep(3); //временная задержка, чтобы проверка оказалась Во время бронирования хода
        List<Booking> bookingList = bookingJpaRepository.findCurrentByOwner(user2.getId(), LocalDateTime.now(), page);

        assertThat(bookingList.size(), equalTo(1)); //один элемент в списке
        assertThat(bookingList.get(0).getId(), equalTo(booking3.getId())); //поля элемента соотв-т ожидаемым
    }
}