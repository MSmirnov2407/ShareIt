package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoWithItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    public UserDto userDto;
    public UserDto userDto2;
    public ItemDto itemDto;
    public Item item;
    public Item item2;
    public Item item3;
    public Booking booking1;
    public Booking booking2;
    public Booking booking3;
    public List<Booking> bookingList;
    @Mock
    static UserService mockUserService;
    @Mock
    static ItemService mockItemService;
    @Mock
    static BookingJpaRepository mockBookingJpaRepository;

    @InjectMocks
    public BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        /*создаем тестовые объекты*/
        userDto = new UserDto();
        userDto.setId(11);
        userDto.setEmail("mail@mail.mail");
        userDto.setName("myName");

        userDto2 = new UserDto();
        userDto2.setId(12);
        userDto2.setEmail("12mail@mail.mail");
        userDto2.setName("12myName");

        itemDto = new ItemDto();
        itemDto.setDescription("описание вещи");
        itemDto.setName("имя вещи");
        itemDto.setIsAvailable(true);
        itemDto.setId(44);
        itemDto.setRequestId(22);
        itemDto.setOwner(userDto);

        item = new Item();
        item.setDescription("описание вещи");
        item.setName("имя вещи");
        item.setIsAvailable(true);
        item.setId(44);
        item.setOwner(UserMapper.dtoToUser(userDto));

        item2 = new Item();
        item2.setDescription("2описание вещи");
        item2.setName("2имя вещи");
        item2.setIsAvailable(true);
        item2.setId(55);
        item2.setOwner(UserMapper.dtoToUser(userDto));

        item3 = new Item();
        item3.setDescription("3описание вещи");
        item3.setName("3имя вещи");
        item3.setIsAvailable(true);
        item3.setId(66);
        item3.setOwner(UserMapper.dtoToUser(userDto));

        booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(UserMapper.dtoToUser(userDto));
        booking1.setId(1);
        booking1.setStatus(Status.WAITING);
        booking1.setStart(LocalDateTime.now().plusMinutes(1));
        booking1.setEnd(LocalDateTime.now().plusDays(1));

        booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setBooker(UserMapper.dtoToUser(userDto));
        booking2.setId(2);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().minusDays(3));
        booking2.setEnd(LocalDateTime.now().minusDays(2));

        booking3 = new Booking();
        booking3.setItem(item3);
        booking3.setBooker(UserMapper.dtoToUser(userDto));
        booking3.setId(3);
        booking3.setStatus(Status.APPROVED);
        booking3.setStart(LocalDateTime.now().plusDays(4));
        booking3.setEnd(LocalDateTime.now().plusDays(5));

        bookingList = Arrays.asList(booking1, booking2, booking3); //список бронирований
    }

    @Test
    void createBookingTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserService.getUserDtoById(userDto2.getId()))
                .thenReturn(userDto2); //при запросе пользователя по id, возвращаем созданного пользователя
        Mockito
                .when(mockItemService.getItemDtoById(item.getId()))
                .thenReturn(itemDto);//при запросе вещи по id, возвращаем созданный ItemDto
        Mockito
                .when(mockBookingJpaRepository.findByItem_IdAndStartIsBeforeAndEndIsAfter(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<Booking>());//при запросе пересекающихся бронирований возвращаем пустой список

        /*вызываем тестируемый метод. Передаем созданный itemDto и id пользователя*/
        BookingDtoWithItem newBookingDtoWithItem = bookingService.createBooking(BookingMapper.bookingToDto(booking1), userDto2.getId());

        /*проверки*/
        assertThat(newBookingDtoWithItem.getId(), equalTo(booking1.getId())); //id равно заданному id в booking1. сохранение в БД не происходит и id не перезаписывается
        assertThat(newBookingDtoWithItem.getItem().getId(), equalTo(item.getId())); //id itema равно заданному id в item
        assertThat(newBookingDtoWithItem.getItem().getOwner().getId(), equalTo(userDto.getId())); //id владельца вещи равно заданному id в userDto
        assertThat(newBookingDtoWithItem.getBooker().getId(), equalTo(userDto2.getId())); //id пользователя равно заданному id в userDto2
    }

    @Test
    void getBookingDtoByIdTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockBookingJpaRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1)); //при запросе Booking по id, возвращаем Optional созданного booking1
        /*вызываем тестируемый метод. Передаем id booking1 и id userDto- владельца вещи. В результате получаем нужный BookingDto*/
        BookingDtoWithItem newBookingDtoWithItem = bookingService.getBookingDtoById(booking1.getId(), userDto.getId());

        /*проверки*/
        assertThat(newBookingDtoWithItem.getId(), equalTo(booking1.getId())); //id равно заданному id в booking1. сохранение в БД не происходит и id не перезаписывается
        assertThat(newBookingDtoWithItem.getItem().getId(), equalTo(item.getId())); //id itema равно заданному id в item
        assertThat(newBookingDtoWithItem.getItem().getOwner().getId(), equalTo(userDto.getId())); //id владельца вещи равно заданному id в userDto
    }

    @Test
    void getAllDtoByUserAndStateTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserService.getUserDtoById(userDto.getId()))
                .thenReturn(userDto); //при запросе пользователя по id, возвращаем созданный userDto
        Mockito
                .when(mockBookingJpaRepository.findByBooker_Id(Mockito.anyInt(), Mockito.any()))
                .thenReturn(bookingList); //бронирований возвращаем подготовленный список
        Mockito
                .when(mockBookingJpaRepository.findByBooker_IdAndEndIsBefore(Mockito.anyInt(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList); //бронирований возвращаем подготовленный список
        Mockito
                .when(mockBookingJpaRepository.findByBooker_IdAndStartIsAfter(Mockito.anyInt(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList); //бронирований возвращаем подготовленный список
        Mockito
                .when(mockBookingJpaRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList); //бронирований возвращаем подготовленный список
        Mockito
                .when(mockBookingJpaRepository.findByBooker_IdAndStatus(Mockito.anyInt(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList); //бронирований возвращаем подготовленный список
        Mockito
                .when(mockBookingJpaRepository.findByBooker_IdAndStatus(Mockito.anyInt(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList); //бронирований возвращаем подготовленный список
        /*вызываем тестируемый метод с параметром ALl*/
        List<BookingDtoWithItem> newBookingDtoWithItemList = bookingService.getAllDtoByUserAndState(1, 2, userDto.getId(), "ALL");
        /*проверки*/
        assertThat(newBookingDtoWithItemList.size(), equalTo(bookingList.size())); //размер полученного списка равен размеру bookingList
        assertThat(newBookingDtoWithItemList.get(0).getId(), equalTo(bookingList.get(0).getId())); //нулевой элемент совпадает с элементом из bookingList

        /*вызываем тестируемый метод с параметром PAST*/
        newBookingDtoWithItemList = bookingService.getAllDtoByUserAndState(1, 2, userDto.getId(), "PAST");
        /*проверки*/
        assertThat(newBookingDtoWithItemList.size(), equalTo(bookingList.size())); //размер полученного списка равен размеру bookingList
        assertThat(newBookingDtoWithItemList.get(0).getId(), equalTo(bookingList.get(0).getId())); //нулевой элемент совпадает с элементом из bookingList

        /*вызываем тестируемый метод с параметром FUTURE*/
        newBookingDtoWithItemList = bookingService.getAllDtoByUserAndState(1, 2, userDto.getId(), "FUTURE");
        /*проверки*/
        assertThat(newBookingDtoWithItemList.size(), equalTo(bookingList.size())); //размер полученного списка равен размеру bookingList
        assertThat(newBookingDtoWithItemList.get(0).getId(), equalTo(bookingList.get(0).getId())); //нулевой элемент совпадает с элементом из bookingList

        /*вызываем тестируемый метод с параметром CURRENT*/
        newBookingDtoWithItemList = bookingService.getAllDtoByUserAndState(1, 2, userDto.getId(), "CURRENT");
        /*проверки*/
        assertThat(newBookingDtoWithItemList.size(), equalTo(bookingList.size())); //размер полученного списка равен размеру bookingList
        assertThat(newBookingDtoWithItemList.get(0).getId(), equalTo(bookingList.get(0).getId())); //нулевой элемент совпадает с элементом из bookingList

        /*вызываем тестируемый метод с параметром WAITING*/
        newBookingDtoWithItemList = bookingService.getAllDtoByUserAndState(1, 2, userDto.getId(), "WAITING");
        /*проверки*/
        assertThat(newBookingDtoWithItemList.size(), equalTo(bookingList.size())); //размер полученного списка равен размеру bookingList
        assertThat(newBookingDtoWithItemList.get(0).getId(), equalTo(bookingList.get(0).getId())); //нулевой элемент совпадает с элементом из bookingList

        /*вызываем тестируемый метод с параметром REJECTED*/
        newBookingDtoWithItemList = bookingService.getAllDtoByUserAndState(1, 2, userDto.getId(), "REJECTED");
        /*проверки*/
        assertThat(newBookingDtoWithItemList.size(), equalTo(bookingList.size())); //размер полученного списка равен размеру bookingList
        assertThat(newBookingDtoWithItemList.get(0).getId(), equalTo(bookingList.get(0).getId())); //нулевой элемент совпадает с элементом из bookingList
    }

    @Test
    void updateBookingTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockBookingJpaRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1)); //при запросе Booking по id, возвращаем Optional созданного booking1
        /*вызываем тестируемый метод. Передаем id booking1 и id userDto- владельца вещи. В результате получаем нужный BookingDto*/
        BookingDtoWithItem newBookingDtoWithItem = bookingService.updateBooking(booking1.getId(), true, userDto.getId());

        /*проверки*/
        assertThat(newBookingDtoWithItem.getId(), equalTo(booking1.getId())); //id равно заданному id в booking1. сохранение в БД не происходит и id не перезаписывается
        assertThat(newBookingDtoWithItem.getItem().getId(), equalTo(item.getId())); //id itema равно заданному id в item
        assertThat(newBookingDtoWithItem.getItem().getOwner().getId(), equalTo(userDto.getId())); //id владельца вещи равно заданному id в userDto
    }
}
