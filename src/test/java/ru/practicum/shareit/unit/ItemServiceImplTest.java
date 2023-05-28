package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    public UserDto userDto;
    public ItemRequest itemRequest;
    public ItemDto itemDto;
    public Item item;
    public Item item2;
    public Item item3;
    public Booking booking1;
    public Booking booking2;
    public Booking booking3;
    public Comment comment1;
    public CommentDto commentDto;
    public List<Item> itemList;
    public List<Booking> bookingList;
    public List<Comment> commentList;
    @Mock
    static UserService mockUserService;
    @Mock
    static ItemJpaRepository mockItemRepository;
    @Mock
    static BookingJpaRepository mockBookingJpaRepository;
    @Mock
    static CommentJpaRepository mockCommentRepository;
    @Mock
    static RequestJpaRepository mockRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService1;

    @BeforeEach
    void setUp() {
        /*создаем тестовые объекты*/
        userDto = new UserDto();
        userDto.setId(11);
        userDto.setEmail("mail@mail.mail");
        userDto.setName("myName");

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("request Description");
        itemRequest.setId(22);

        itemDto = new ItemDto();
        itemDto.setDescription("описание вещи");
        itemDto.setName("имя вещи");
        itemDto.setIsAvailable(true);
        itemDto.setId(33);
        itemDto.setRequestId(22);

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
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(1));

        booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setBooker(UserMapper.dtoToUser(userDto));
        booking2.setId(2);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));

        booking3 = new Booking();
        booking3.setItem(item3);
        booking3.setBooker(UserMapper.dtoToUser(userDto));
        booking3.setId(3);
        booking3.setStatus(Status.APPROVED);
        booking3.setStart(LocalDateTime.now().plusDays(4));
        booking3.setEnd(LocalDateTime.now().plusDays(5));

        comment1 = new Comment();
        comment1.setItem(item);
        comment1.setText("комментарий 1");
        comment1.setAuthor(UserMapper.dtoToUser(userDto));
        comment1.setId(1);
        comment1.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setItemId(44);
        commentDto.setAuthorName("author");

        itemList = Arrays.asList(item, item2, item3);
        bookingList = Arrays.asList(booking1, booking2, booking3); //список бронирований
        commentList = Arrays.asList(comment1); //список бронирований
    }

    @Test
    void createItemTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserService.getUserDtoById(11))
                .thenReturn(userDto); //при запросе пользователя по id, возвращаем созданного пользователя
        Mockito
                .when(mockRequestRepository.findById(22))
                .thenReturn(itemRequest);//при запросе requesta по id, возвращаем созданный request

        /*вызываем тестируемый метод. Передаем созданный itemDto и id пользователя*/
        ItemDto newItemDto = itemService1.createItem(itemDto, 11);

        /*проверки*/
        assertThat(newItemDto.getName(), equalTo("имя вещи")); //имя равно заданному имени в itemDto
        assertThat(newItemDto.getId(), equalTo(33)); //id равно заданному id в itemDto. сохранение в БД не происходит и id не перезаписывается
        assertThat(newItemDto.getOwner().getId(), equalTo(userDto.getId())); //присвоенный пользователь равен созданному userDto
        assertThat(newItemDto.getRequestId(), equalTo(itemRequest.getId()));//присвоенный запрос равен созданному itemRequest
    }

    @Test
    void getItemDtoByIdTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockItemRepository.findById(44))
                .thenReturn(Optional.of(item)); //при запросе Item по id, возвращаем Optional созданного itema
        /*вызываем тестируемый метод. Передаем id вещи. В результате получаем нужный ItemDto*/
        ItemDto newItemDto = itemService1.getItemDtoById(44);

        /*проверки*/
        assertThat(newItemDto.getName(), equalTo("имя вещи")); //имя равно заданному имени в item
        assertThat(newItemDto.getDescription(), equalTo("описание вещи")); //описание равно заданному в item
        assertThat(newItemDto.getId(), equalTo(44)); //id равно заданному id в item. сохранение в БД не происходит и id не перезаписывается
    }

    @Test
    void getAllDtoByUserTest() {

        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockItemRepository.findAllByOwnerId(Mockito.anyInt(), Mockito.any()))
                .thenReturn(itemList); //при запросе списка Item по id владельца, возвращаем созданный списко itemList
        Mockito
                .when(mockBookingJpaRepository.findByItem_IdIn(Mockito.anyList(), Mockito.any()))
                .thenReturn(bookingList); //при запросе списка бронирований вещей, возвращаем подготоваленный список бронирований
        Mockito
                .when(mockCommentRepository.findByItem_IdIn(Mockito.anyList(), Mockito.any()))
                .thenReturn(commentList); //при запросе списка комментариев для вещей, возвращаем подготоваленный список комментариев


        /*вызываем тестируемый метод. Передаем рандомные параметры,т.к.они не используются. В результате получаем список ItemDto*/
        List<ItemDtoWithBookings> itemDtoList = itemService1.getAllDtoByUser(1, 2, 11);

        /*проверки*/
        assertThat(itemDtoList.size(), equalTo(3)); //размер списка соответствует перечню itemов
        assertThat(itemDtoList.get(0).getId(), equalTo(itemList.get(0).getId())); //id первого элемента соотв. первой вещи из списка
        assertThat(itemDtoList.get(0).getLastBooking().getStart(), equalTo(bookingList.get(0).getStart())); //последнее бронирование - это booking1
        assertThat(itemDtoList.get(0).getNextBooking(), equalTo(null)); //след. бронирования нету

        assertThat(itemDtoList.get(1).getId(), equalTo(itemList.get(1).getId())); //id второго элемента соотв. второй вещи из списка
        assertThat(itemDtoList.get(1).getLastBooking(), equalTo(null)); //предыдущ. бронирования нету
        assertThat(itemDtoList.get(1).getNextBooking().getStart(), equalTo(bookingList.get(1).getStart())); //след. бронирование - это booking2

        assertThat(itemDtoList.get(2).getId(), equalTo(itemList.get(2).getId())); //id третьего элемента соотв. третьей вещи из списка
        assertThat(itemDtoList.get(2).getLastBooking(), equalTo(null)); //предыдущ. бронирования нету
        assertThat(itemDtoList.get(2).getNextBooking().getStart(), equalTo(bookingList.get(2).getStart())); //след. бронирование - это booking3
    }

    @Test
    void updateItemTest() {

        Mockito
                .when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item)); //при запросе Item по id, возвращаем Optional созданного item
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(item); //при сохранении вещи , возвращаем просто ее же

        /*вызываем тестируемый метод*/
        ItemDto updatedItemDto = itemService1.updateItem(itemDto, 44, 11);

        /*проверяем количество вызовов методов*/
        Mockito.verify(mockItemRepository, Mockito.times(2))
                .findById(item.getId());
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .save(Mockito.any());

        assertThat(updatedItemDto.getId(), equalTo(item.getId()));
    }

    @Test
    void searchItemsDtoTest() {

        Mockito
                .when(mockItemRepository.searchItems(Mockito.anyString(), Mockito.any()))
                .thenReturn(itemList); //при поиске вещей, возвращаем созданный itemList

        /*вызываем тестируемый метод*/
        List<ItemDto> itemsDto = itemService1.searchItemsDto(1, 2, "aaa");

        /*проверяем только количество вызовов методов*/
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .searchItems(Mockito.anyString(), Mockito.any());

        assertThat(itemsDto.get(0).getId(), equalTo(itemList.get(0).getId())); //первый DTO соответствует первой вещи из списка
        assertThat(itemsDto.get(1).getId(), equalTo(itemList.get(1).getId())); //второй DTO соответствует второй вещи из списка
        assertThat(itemsDto.get(2).getId(), equalTo(itemList.get(2).getId())); //третий DTO соответствует третьей вещи из списка
    }

    @Test
    void createCommentTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserService.getUserDtoById(11))
                .thenReturn(userDto); //при запросе пользователя по id, возвращаем созданного пользователя
        Mockito
                .when(mockCommentRepository.save(Mockito.any()))
                .thenReturn(null);//просто заглушка для мтеода, т.к. его результат не используется
        Mockito
                .when(mockItemRepository.findById(44))
                .thenReturn(Optional.of(item)); //при запросе Item по id, возвращаем Optional созданного itema
        Mockito
                .when(mockItemRepository.findById(44))
                .thenReturn(Optional.of(item)); //при запросе Item по id, возвращаем Optional созданного itema
        Mockito
                .when(mockBookingJpaRepository.findByItem_idAndBooker_IdAndEndIsBefore(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList); //при запросе списка бронирований вещей, возвращаем подготоваленный список бронирований.

        /*вызываем тестируемый метод*/
        CommentDto newCommentDto = itemService1.createComment(44, 11, commentDto);

        /*проверки*/
        assertThat(newCommentDto.getId(), equalTo(commentDto.getId())); //id равно заданному id в commentDto. сохранение в БД не происходит и id не перезаписывается
    }

}
