package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional //чтобы после каждого теста выполнялся RollBack
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class RequestServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final RequestService requestService;
    public UserDto userDto;
    public UserDto userDto2;
    public ItemRequestDto itemRequestDto;
    public ItemDto itemDto;
    public ItemDto itemDto1;
    public ItemDto itemDto2;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setEmail("mail@mail.mail");
        userDto.setName("myName");

        userDto2 = new UserDto();
        userDto2.setEmail("2mail@mail.mail");
        userDto2.setName("2myName");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");

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
    }

    @Test
    void createRequestTest() {
        UserDto createdUserDto = userService.createUser(userDto); //создали юзера

        /*вызов тестируемого метода */
        ItemRequestDto createdRequestDto = requestService.createRequest(itemRequestDto, createdUserDto.getId());

        /*проверки*/
        assertThat(createdRequestDto.getDescription(), equalTo(itemRequestDto.getDescription())); //описания совпадают
        assertThat(createdRequestDto.getRequestor().getId(), equalTo(createdUserDto.getId())); //id пользователя совпадают
    }

    @Test
    void getAllDtoWithAnswByUserIdTest() {
        UserDto createdUserDto = userService.createUser(userDto); //создали юзера
        UserDto createdUserDto2 = userService.createUser(userDto2); //создали юзера

        ItemRequestDto createdRequestDto1 = requestService.createRequest(itemRequestDto, createdUserDto.getId()); //создали несколько запросов
        ItemRequestDto createdRequestDto2 = requestService.createRequest(itemRequestDto, createdUserDto.getId());
        ItemRequestDto createdRequestDto3 = requestService.createRequest(itemRequestDto, createdUserDto.getId());

        itemDto.setRequestId(createdRequestDto1.getId()); //присвоили id запросов к вещам
        itemDto1.setRequestId(createdRequestDto1.getId());
        itemDto2.setRequestId(createdRequestDto2.getId());

        itemService.createItem(itemDto, createdUserDto2.getId()); //создали ответы на запросы
        itemService.createItem(itemDto1, createdUserDto2.getId());
        itemService.createItem(itemDto2, createdUserDto2.getId());

        /*вызов тестируемого метода */
        List<ItemRequestDtoWithAnswer> requestDtoWithAnswerList = requestService.getAllDtoWithAnswByUserId(createdUserDto.getId());

        /*проверки*/
        assertThat(requestDtoWithAnswerList.size(), equalTo(3)); //кол-во совпадает с созданными запросами
        assertThat(requestDtoWithAnswerList.get(0).getId(), equalTo(createdRequestDto1.getId())); //id запросов совпадают
        assertThat(requestDtoWithAnswerList.get(1).getId(), equalTo(createdRequestDto2.getId())); //id запросов совпадают
        assertThat(requestDtoWithAnswerList.get(2).getId(), equalTo(createdRequestDto3.getId())); //id запросов совпадают
        assertThat(requestDtoWithAnswerList.get(0).getItems().size(), equalTo(2)); //у нулевого createdRequestDto1 Две вещи-ответа
        assertThat(requestDtoWithAnswerList.get(1).getItems().size(), equalTo(1)); //у первого createdRequestDto1 одна вещи-ответа
        assertThat(requestDtoWithAnswerList.get(2).getItems().size(), equalTo(0)); //у второго createdRequestDto1 ноль вещеи-ответов
    }
}
