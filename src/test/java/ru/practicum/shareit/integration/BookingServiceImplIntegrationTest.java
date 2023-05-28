package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItem;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional //чтобы после каждого теста выполнялся RollBack
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class BookingServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final RequestService requestService;
    private final BookingService bookingService;

    public static UserDto userDto;
    public static UserDto userDto2;
    public ItemDto itemDto;
    private BookingDto bookingDto;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;
    private CommentDto commentDto;
    private ItemDto itemDto1;
    private ItemDto itemDto2;

    @BeforeEach
    void setUp() {
        /*создаем тестовые объекты*/
        userDto = new UserDto();
        userDto.setEmail("mail@mail.mail");
        userDto.setName("myName");

        userDto2 = new UserDto();
        userDto2.setEmail("2mail@mail.mail");
        userDto2.setName("2myName");

        itemDto = new ItemDto();
        itemDto.setDescription("описание вещи поиск ");
        itemDto.setName("имя вещи");
        itemDto.setIsAvailable(true);

        itemDto1 = new ItemDto();
        itemDto1.setDescription("1описание вещи");
        itemDto1.setName("1имя вещи");
        itemDto1.setIsAvailable(true);

        itemDto2 = new ItemDto();
        itemDto2.setDescription("2описание вещи поисковик");
        itemDto2.setName("2имя вещи");
        itemDto2.setIsAvailable(true);

        bookingDto = new BookingDto();
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));

        bookingDto1 = new BookingDto();
        bookingDto1.setStatus(Status.WAITING);
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(3));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(4));

        bookingDto2 = new BookingDto();
        bookingDto2.setStatus(Status.WAITING);
        bookingDto2.setStart(LocalDateTime.now().plusSeconds(5));
        bookingDto2.setEnd(LocalDateTime.now().plusSeconds(6));

        commentDto = new CommentDto();
        commentDto.setText("text text");
    }

    @Test
    void createBookingTest() {
        UserDto booker = userService.createUser(userDto); //создали юзера
        UserDto owner = userService.createUser(userDto2); //создали юзера
        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId()); //создали вещь
        bookingDto.setItemId(newItemDto.getId()); //происовили id вещи в бронирование
        /*вызов тестируемого метода */
        BookingDtoWithItem newBookingDtoWithItem = bookingService.createBooking(bookingDto, booker.getId());

        /*проверки*/
        assertThat(newBookingDtoWithItem.getItem().getName(), equalTo(newItemDto.getName())); //названия вещей совпадают

        newItemDto.setIsAvailable(false); //изменили статус вещи
        itemService.updateItem(newItemDto, newItemDto.getId(), owner.getId()); //оновили вещь
        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBooking(bookingDto, booker.getId())); //исключение при недоступной вещи
    }

    @Test
    void getBookingDtoByIdTest() {
        UserDto booker = userService.createUser(userDto); //создали юзера
        UserDto owner = userService.createUser(userDto2); //создали юзера
        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId()); //создали вещь
        bookingDto.setItemId(newItemDto.getId()); //происовили id вещи в бронирование
        BookingDtoWithItem createdBookingDtoWithItem = bookingService.createBooking(bookingDto, booker.getId());

        /*вызов тестируемого метода */
        BookingDtoWithItem newBookingDtoWithItem = bookingService.getBookingDtoById(createdBookingDtoWithItem.getId(), booker.getId());

        /*проверки*/
        assertThat(newBookingDtoWithItem.getItem().getName(), equalTo(newItemDto.getName())); //названия вещей совпадают
        assertThrows(ElementNotFoundException.class,
                () -> bookingService.getBookingDtoById(99, booker.getId())); //исключение при неверном id боронирования
        assertThrows(NotAnOwnerException.class,
                () -> bookingService.getBookingDtoById(createdBookingDtoWithItem.getId(), 99)); //исключение при неверном id юзера
    }

    @Test
    void getAllDtoByUserAndStateTest() {
        UserDto booker = userService.createUser(userDto); //создали юзера
        UserDto owner = userService.createUser(userDto2); //создали юзера
        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId()); //создали вещь
        bookingDto.setItemId(newItemDto.getId()); //происовили id вещи в бронирование
        bookingDto1.setItemId(newItemDto.getId()); //происовили id вещи в бронирование
        bookingDto2.setItemId(newItemDto.getId()); //происовили id вещи в бронирование
        BookingDtoWithItem createdBookingDtoWithItem = bookingService.createBooking(bookingDto, booker.getId()); //создали несколько бронирований
        BookingDtoWithItem createdBookingDtoWithItem2 = bookingService.createBooking(bookingDto1, booker.getId());
        BookingDtoWithItem createdBookingDtoWithItem3 = bookingService.createBooking(bookingDto2, booker.getId());

        //bookingDto.setStatus(Status.REJECTED); //изменили статус запроса на отклоненный
        bookingService.updateBooking(createdBookingDtoWithItem.getId(), false, owner.getId()); //обновили данные о бронировании

        /*вызов тестируемого метода */
        List<BookingDtoWithItem> newBookingDtoWithItemRejected = bookingService.getAllDtoByUserAndState(0, 5, booker.getId(), "REJECTED");
        List<BookingDtoWithItem> newBookingDtoWithItemWaiting = bookingService.getAllDtoByUserAndState(0, 2, booker.getId(), "WAITING");
        List<BookingDtoWithItem> newBookingDtoWithItemAll = bookingService.getAllDtoByUserAndState(0, 5, booker.getId(), "ALL");
        List<BookingDtoWithItem> newBookingDtoWithItemAllSize2 = bookingService.getAllDtoByUserAndState(0, 2, booker.getId(), "ALL");

        /*проверки*/
        assertThat(newBookingDtoWithItemRejected.size(), equalTo(1)); //в списке одно отклоненное бронирование
        assertThat(newBookingDtoWithItemWaiting.size(), equalTo(2)); //в списке два ожидающих подтверждения бронирование
        assertThat(newBookingDtoWithItemAll.size(), equalTo(3)); //в списке всего три бронирования
        assertThat(newBookingDtoWithItemAllSize2.size(), equalTo(2)); //проверка пагинации

        assertThrows(PaginationParametersException.class,
                () -> bookingService.getAllDtoByUserAndState(-1, 5, booker.getId(), "REJECTED")); //исключение при неверном from
        assertThrows(PaginationParametersException.class,
                () -> bookingService.getAllDtoByUserAndState(0, -1, booker.getId(), "REJECTED")); //исключение при неверном size
        assertThrows(ElementNotFoundException.class,
                () -> bookingService.getAllDtoByUserAndState(0, 5, 99, "REJECTED")); //исключение при неверном id bookera
        assertThrows(StateNotAllowedException.class,
                () -> bookingService.getAllDtoByUserAndState(0, 5, booker.getId(), "wrong")); //исключение при неверном статусе
    }

    @Test
    void getAllDtoByOwnerAndStateTest() {
        UserDto booker = userService.createUser(userDto); //создали юзера
        UserDto owner = userService.createUser(userDto2); //создали юзера
        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId()); //создали вещь
        ItemDto newItemDto2 = itemService.createItem(itemDto2, booker.getId()); //создали вещь
        bookingDto.setItemId(newItemDto.getId()); //происовили id вещи в бронирование
        bookingDto1.setItemId(newItemDto2.getId()); //происовили id вещи в бронирование
        bookingDto2.setItemId(newItemDto2.getId()); //происовили id вещи в бронирование
        BookingDtoWithItem createdBookingDtoWithItem = bookingService.createBooking(bookingDto, booker.getId()); //создали несколько бронирований
        BookingDtoWithItem createdBookingDtoWithItem2 = bookingService.createBooking(bookingDto1, owner.getId());
        BookingDtoWithItem createdBookingDtoWithItem3 = bookingService.createBooking(bookingDto2, owner.getId());

        bookingService.updateBooking(createdBookingDtoWithItem2.getId(), false, booker.getId()); //обновили данные о бронировании

        /*вызов тестируемого метода */
        List<BookingDtoWithItem> newBookingDtoWithItemRejected = bookingService.getAllDtoByOwnerAndState(0, 5, booker.getId(), "REJECTED");
        List<BookingDtoWithItem> newBookingDtoWithItemWaiting = bookingService.getAllDtoByOwnerAndState(0, 2, booker.getId(), "WAITING");
        List<BookingDtoWithItem> newBookingDtoWithItemAll = bookingService.getAllDtoByOwnerAndState(0, 5, booker.getId(), "ALL");
        List<BookingDtoWithItem> newBookingDtoWithItemAllSize2 = bookingService.getAllDtoByOwnerAndState(0, 1, booker.getId(), "ALL");

        /*проверки*/
        assertThat(newBookingDtoWithItemRejected.size(), equalTo(1)); //в списке одно отклоненное бронирование
        assertThat(newBookingDtoWithItemWaiting.size(), equalTo(1)); //в списке два ожидающих подтверждения бронирование
        assertThat(newBookingDtoWithItemAll.size(), equalTo(2)); //в списке всего три бронирования
        assertThat(newBookingDtoWithItemAllSize2.size(), equalTo(1)); //проверка пагинации

        assertThrows(PaginationParametersException.class,
                () -> bookingService.getAllDtoByOwnerAndState(-1, 5, booker.getId(), "REJECTED")); //исключение при неверном from
        assertThrows(PaginationParametersException.class,
                () -> bookingService.getAllDtoByOwnerAndState(0, -1, booker.getId(), "REJECTED")); //исключение при неверном size
        assertThrows(ElementNotFoundException.class,
                () -> bookingService.getAllDtoByOwnerAndState(0, 5, 99, "REJECTED")); //исключение при неверном id bookera
        assertThrows(StateNotAllowedException.class,
                () -> bookingService.getAllDtoByOwnerAndState(0, 5, booker.getId(), "wrong")); //исключение при неверном статусе
    }

    @Test
    void updateBookingTest() {
        UserDto booker = userService.createUser(userDto); //создали юзера
        UserDto owner = userService.createUser(userDto2); //создали юзера
        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId()); //создали вещь
        bookingDto.setItemId(newItemDto.getId()); //происовили id вещи в бронирование
        BookingDtoWithItem createdBookingDtoWithItem = bookingService.createBooking(bookingDto, booker.getId()); //создали  бронирование

        /*вызов тестируемого метода */
        BookingDtoWithItem updatedBooking = bookingService.updateBooking(createdBookingDtoWithItem.getId(), false, owner.getId()); //обновили данные о бронировании

        /*проверки*/
        assertThat(updatedBooking.getId(), equalTo(createdBookingDtoWithItem.getId())); //id не изменился
        assertNotEquals(updatedBooking.getStatus(), (createdBookingDtoWithItem.getStatus())); //статусы не совпадают


        assertThrows(BookingAlreadyApprovedException.class,
                () -> bookingService.updateBooking(createdBookingDtoWithItem.getId(), false, owner.getId())); //исключение при повторном подтверждении
    }

}
