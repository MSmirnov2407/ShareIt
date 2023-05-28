package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    public UserDto userDto;
    public ItemRequest itemRequest;
    public ItemRequest itemRequest2;
    public ItemRequest itemRequest3;
    public ItemRequestDto itemRequestDto;
    public Item item;
    public Item item2;
    public Item item3;
    public List<Item> itemList;
    public List<ItemRequest> requestList;
    @Mock
    static UserService mockUserService;
    @Mock
    static ItemJpaRepository mockItemRepository;
    @Mock
    static RequestJpaRepository mockRequestRepository;
    @InjectMocks
    public RequestServiceImpl requestService;

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
        itemRequest.setRequestor(UserMapper.dtoToUser(userDto));

        itemRequest2 = new ItemRequest();
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("request Description");
        itemRequest2.setId(88);
        itemRequest2.setRequestor(UserMapper.dtoToUser(userDto));

        itemRequest3 = new ItemRequest();
        itemRequest3.setCreated(LocalDateTime.now());
        itemRequest3.setDescription("request Description");
        itemRequest3.setId(99);
        itemRequest3.setRequestor(UserMapper.dtoToUser(userDto));

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("request Description");
        itemRequestDto.setId(22);

        item = new Item();
        item.setDescription("описание вещи");
        item.setName("имя вещи");
        item.setIsAvailable(true);
        item.setId(44);
        item.setOwner(UserMapper.dtoToUser(userDto));
        item.setRequest(itemRequest);

        item2 = new Item();
        item2.setDescription("2описание вещи");
        item2.setName("2имя вещи");
        item2.setIsAvailable(true);
        item2.setId(55);
        item2.setOwner(UserMapper.dtoToUser(userDto));
        item2.setRequest(itemRequest2);

        item3 = new Item();
        item3.setDescription("3описание вещи");
        item3.setName("3имя вещи");
        item3.setIsAvailable(true);
        item3.setId(66);
        item3.setOwner(UserMapper.dtoToUser(userDto));
        item3.setRequest(itemRequest3);

        itemList = Arrays.asList(item, item2, item3);
        requestList = Arrays.asList(itemRequest, itemRequest2, itemRequest3); // список запросов
    }

    @Test
    void createRequestTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserService.getUserDtoById(11))
                .thenReturn(userDto); //при запросе пользователя по id, возвращаем созданного пользователя
        Mockito
                .when(mockRequestRepository.save(Mockito.any()))
                .thenReturn(itemRequest);//при сохранении requesta по id, возвращаем его же

        /*вызываем тестируемый метод. Передаем созданный requestDto и id пользователя*/
        ItemRequestDto newRequestDto = requestService.createRequest(itemRequestDto, 11);

        /*проверки*/
        assertThat(newRequestDto.getId(), equalTo(itemRequest.getId())); //id равно заданному id в requestDto. сохранение в БД не происходит и id не перезаписывается
        assertThat(newRequestDto.getRequestor().getId(), equalTo(userDto.getId())); //присвоенный пользователь равен созданному userDto
    }

    @Test
    void getAllDtoWithAnswByUserIdTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserService.getUserDtoById(11))
                .thenReturn(userDto); //при запросе пользователя по id, возвращаем созданного пользователя
        Mockito
                .when(mockRequestRepository.findAllByRequestor_Id(11, Sort.by("created").descending()))
                .thenReturn(requestList);//при выгрузке запросов возвращаем созданный список
        Mockito
                .when(mockItemRepository.findByRequest_IdIn(Mockito.anyList()))
                .thenReturn(itemList);//при выгрузке Списка вещей возвращаем созданный список

        /*вызываем тестируемый метод. Передаем созданный requestDto и id пользователя*/
        List<ItemRequestDtoWithAnswer> newRequestDtoList = requestService.getAllDtoWithAnswByUserId(11);

        /*проверки*/
        assertThat(newRequestDtoList.size(), equalTo(requestList.size())); //размер списка совпадает с размером созданного списка
        assertThat(newRequestDtoList.get(0).getId(), equalTo(requestList.get(0).getId())); //id 0-го элемента совпадает с переданным
    }

    @Test
    void getAllDtoWithAnswTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockRequestRepository.findAllByRequestor_IdNot(Mockito.anyInt(), Mockito.any()))
                .thenReturn(requestList);//при выгрузке запросов возвращаем созданный список
        Mockito
                .when(mockItemRepository.findByRequest_IdIn(Mockito.anyList()))
                .thenReturn(itemList);//при выгрузке Списка вещей возвращаем созданный список

        /*вызываем тестируемый метод.*/
        List<ItemRequestDtoWithAnswer> newRequestDtoList = requestService.getAllDtoWithAnsw(1, 2, 22);

        /*проверки*/
        assertThat(newRequestDtoList.size(), equalTo(requestList.size())); //размер списка совпадает с размером созданного списка
        assertThat(newRequestDtoList.get(0).getId(), equalTo(requestList.get(0).getId())); //id 0-го элемента совпадает с переданным
    }

    @Test
    void getDtoWithAnswByIdTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserService.getUserDtoById(11))
                .thenReturn(userDto); //при запросе пользователя по id, возвращаем созданного пользователя
        Mockito
                .when(mockRequestRepository.findById(22))
                .thenReturn(itemRequest);//при выгрузке запроса по id, возвращаем созданный запрос
        Mockito
                .when(mockItemRepository.findByRequest_IdIn(Mockito.anyList()))
                .thenReturn(itemList);//при выгрузке Списка вещей возвращаем созданный список

        /*вызываем тестируемый метод. Передаем id запроса и id пользователя*/
        ItemRequestDtoWithAnswer newRequestDto = requestService.getDtoWithAnswById(22, 11);

        /*проверки*/
        assertThat(newRequestDto.getId(), equalTo(itemRequest.getId())); //id элемента совпадает с переданным
    }
}
