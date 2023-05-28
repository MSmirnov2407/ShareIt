package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoWithItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.NotAnOwnerException;
import ru.practicum.shareit.exception.PaginationParametersException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional //чтобы после каждого теста выполнялся RollBack
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    public static UserDto userDto;
    public static UserDto userDto2;
    public ItemDto itemDto;
    private Booking booking1;
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

        booking1 = new Booking();
        booking1.setItem(ItemMapper.dtoToItem(itemDto));
        booking1.setBooker(UserMapper.dtoToUser(userDto));
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().plusSeconds(1));
        booking1.setEnd(LocalDateTime.now().plusSeconds(2));

        commentDto = new CommentDto();
        commentDto.setText("text text");
    }

    @Test
    void createItemTest() {
        UserDto owner = userService.createUser(userDto); //создали юзера

        /*вызов тестируемого метода */
        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId());

        /*проверки*/
        assertThat(newItemDto.getName(), equalTo(itemDto.getName())); //названия совпадают
        assertThat(newItemDto.getOwner().getId(), equalTo(owner.getId())); //владельцы совпадают

        assertThrows(ElementNotFoundException.class,
                () -> itemService.createItem(itemDto, 99)); //исключение при неправильном id владельца
    }

    @Test
    void getItemDtoByIdTest() {
        UserDto owner = userService.createUser(userDto); //создали юзера

        ItemDto createdItem = itemService.createItem(itemDto, owner.getId()); //Создали вещь

        /*вызов тестируемого метода */
        ItemDto newItemDto = itemService.getItemDtoById(createdItem.getId());

        /*проверки*/
        assertThat(newItemDto.getName(), equalTo(itemDto.getName())); //названия совпадают
        assertThat(newItemDto.getOwner().getId(), equalTo(owner.getId())); //владельцы совпадают

        assertThrows(ElementNotFoundException.class,
                () -> itemService.getItemDtoById(99)); //исключение при неправильном id
    }

    @Test
    @SneakyThrows
//чтобы не обрабатывать исключения
    void getItemDtoWithBookingsByIdTest() {
        UserDto owner = userService.createUser(userDto); //создали юзера
        UserDto booker = userService.createUser(userDto2); //создали юзера

        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId()); //Создали вещь
        booking1.setItem(ItemMapper.dtoToItem(newItemDto)); //присвоили вещь к бронированию

        BookingDtoWithItem booking = bookingService.createBooking(BookingMapper.bookingToDto(booking1), booker.getId()); //создали бронирование
        TimeUnit.SECONDS.sleep(3); //временная задержка, чтобы создание комментария оказалось ПОСЛЕ окончания бронирования
        CommentDto comment = itemService.createComment(newItemDto.getId(), booker.getId(), commentDto); //создали коммент

        /*вызов тестируемого метода */
        ItemDtoWithBookings itemDtoWithBookings = itemService.getItemDtoWithBookingsById(newItemDto.getId(), owner.getId());

        /*проверки*/
        assertThat(itemDtoWithBookings.getName(), equalTo(itemDto.getName())); //названия совпадают
        assertThat(itemDtoWithBookings.getOwner().getId(), equalTo(owner.getId())); //владельцы совпадают
        assertThat(itemDtoWithBookings.getLastBooking().getId(), equalTo(booking.getId())); //бронирование совпадают
        assertThat(itemDtoWithBookings.getComments().size(), equalTo(1)); //всего один коммментарий
        assertThat(itemDtoWithBookings.getComments().get(0).getId(), equalTo(comment.getId())); // коммментарий совпадает

        assertThrows(ElementNotFoundException.class,
                () -> itemService.getItemDtoWithBookingsById(99, 11)); //исключение при неправильном id
    }

    @Test
    @SneakyThrows
//чтобы не обрабатывать исключения
    void getAllDtoByUserTest() {
        UserDto owner = userService.createUser(userDto); //создали юзера
        UserDto booker = userService.createUser(userDto2); //создали юзера

        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId()); //Создали вещь
        ItemDto newItemDto2 = itemService.createItem(itemDto1, owner.getId()); //Создали вещь
        ItemDto newItemDto3 = itemService.createItem(itemDto2, owner.getId()); //Создали вещь

        booking1.setItem(ItemMapper.dtoToItem(newItemDto)); //присвоили вещь к бронированию

        BookingDtoWithItem booking = bookingService.createBooking(BookingMapper.bookingToDto(booking1), booker.getId()); //создали бронирование
        TimeUnit.SECONDS.sleep(3); //временная задержка, чтобы создание комментария оказалось ПОСЛЕ окончания бронирования
        CommentDto comment = itemService.createComment(newItemDto.getId(), booker.getId(), commentDto); //создали коммент

        /*вызов тестируемого метода */
        List<ItemDtoWithBookings> itemDtoWithBookingsList = itemService.getAllDtoByUser(0, 2, owner.getId());

        /*проверки*/
        assertThat(itemDtoWithBookingsList.size(), equalTo(2)); //размер списка совпадает с параметром пагинации
        assertThat(itemDtoWithBookingsList.get(0).getId(), equalTo(newItemDto.getId())); //Нулевой элемент - это первый добавленый itemDto
        assertThat(itemDtoWithBookingsList.get(0).getLastBooking().getId(), equalTo(booking.getId())); //В Нулевой элемент добавлено правильное бронирование

        assertThrows(PaginationParametersException.class,
                () -> itemService.getAllDtoByUser(-1, 2, 1)); //исключение при неправильном from
        assertThrows(PaginationParametersException.class,
                () -> itemService.getAllDtoByUser(0, -1, 1)); //исключение при неправильном size
    }

    @Test
    void deleteItemTest() {
        UserDto owner = userService.createUser(userDto); //создали юзера

        ItemDto createdItem = itemService.createItem(itemDto, owner.getId()); //Создали вещь

        /*вызов тестируемого метода */
        itemService.deleteItem(createdItem.getId());

        /*проверки*/
        assertThrows(ElementNotFoundException.class,
                () -> itemService.getItemDtoById(createdItem.getId())); //исключение т.к. элемент удален
    }

    @Test
    void updateItemTest() {
        UserDto owner = userService.createUser(userDto); //создали юзера

        ItemDto createdItem = itemService.createItem(itemDto, owner.getId()); //Создали вещь
        createdItem.setName("updated name"); //изменили имя

        /*вызов тестируемого метода */
        itemService.updateItem(createdItem, createdItem.getId(), owner.getId());

        ItemDto updatedItemDto = itemService.getItemDtoById(createdItem.getId()); //взяли из хранилища обнлвленную вещь по id

        /*проверки*/
        assertThat(updatedItemDto.getName(), equalTo("updated name")); //названия совпадают

        assertThrows(NotAnOwnerException.class,
                () -> itemService.updateItem(createdItem, createdItem.getId(), 99)); //исключение при неправильном id владльца
    }


    @Test
    void searchItemsDtoTest() {
        UserDto owner = userService.createUser(userDto); //создали юзера
        UserDto booker = userService.createUser(userDto2); //создали юзера

        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId()); //Создали вещь
        ItemDto newItemDto2 = itemService.createItem(itemDto1, owner.getId()); //Создали вещь
        ItemDto newItemDto3 = itemService.createItem(itemDto2, owner.getId()); //Создали вещь

        List<ItemDto> itemDtoList = itemService.searchItemsDto(0, 2, "поиск");

        /*проверки*/
        assertThat(itemDtoList.size(), equalTo(2)); //размер списка совпадает с параметром пагинации
        assertTrue(itemDtoList.get(0).getDescription().contains("поиск"));
        ; //Нулевой элемент - содержит поисковый текст
        assertTrue(itemDtoList.get(1).getDescription().contains("поиск"));
        ; //Первый элемент - содержит поисковый текст

        assertThrows(PaginationParametersException.class,
                () -> itemService.getAllDtoByUser(-1, 2, 1)); //исключение при неправильном from
        assertThrows(PaginationParametersException.class,
                () -> itemService.getAllDtoByUser(0, -1, 1)); //исключение при неправильном size
    }

    @Test
    @SneakyThrows
        //чтобы не обрабатывать исключения
    void createCommentTest() {
        UserDto owner = userService.createUser(userDto); //создали юзера
        UserDto booker = userService.createUser(userDto2); //создали юзера

        ItemDto newItemDto = itemService.createItem(itemDto, owner.getId()); //Создали вещь

        booking1.setItem(ItemMapper.dtoToItem(newItemDto)); //присвоили вещь к бронированию

        BookingDtoWithItem booking = bookingService.createBooking(BookingMapper.bookingToDto(booking1), booker.getId()); //создали бронирование
        TimeUnit.SECONDS.sleep(3); //временная задержка, чтобы создание комментария оказалось ПОСЛЕ окончания бронирования

        /*вызов тестируемого метода */
        CommentDto newComment = itemService.createComment(newItemDto.getId(), booker.getId(), commentDto); //создали коммент

        ItemDtoWithBookings itemWithComment = itemService.getItemDtoWithBookingsById(newItemDto.getId(), owner.getId()); //запросили вещь по id

        /*проверки*/
        assertThat(itemWithComment.getComments().get(0).getId(), equalTo(newComment.getId())); //коммент в выгруженной вещи совпадает с сохраненным
        assertThat(itemWithComment.getComments().get(0).getText(), equalTo(newComment.getText())); //коммент в выгруженной вещи совпадает с сохраненным
    }
}
