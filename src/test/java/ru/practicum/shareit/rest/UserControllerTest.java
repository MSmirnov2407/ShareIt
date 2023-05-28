package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutBooker;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper; //отвечает за преобразование объектов
    @Autowired
    private MockMvc mockMvc; //отвечает за создание запросов
    @MockBean
    private UserService userService;

    public UserDto userDto;
    public UserDto userDto2;

    public ItemDtoWithBookings itemDtoWithBookings;
    public ItemDto itemDto;
    public CommentDto commentDto;


    @BeforeEach
    void setUp() {
        /*создаем тестовые объекты*/
        userDto = new UserDto();
        userDto.setEmail("mail@mail.mail");
        userDto.setName("myName");

        userDto2 = new UserDto();
        userDto2.setEmail("2mail@mail.mail");
        userDto2.setName("2myName");

        itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setDescription("description");
        itemDtoWithBookings.setName("itemname");
        itemDtoWithBookings.setIsAvailable(true);
        itemDtoWithBookings.setOwner(userDto);
        itemDtoWithBookings.setId(2);
        itemDtoWithBookings.setLastBooking(new BookingDtoWithoutBooker());

        itemDto = new ItemDto();
        itemDto.setDescription("description2");
        itemDto.setName("itemname2");
        itemDto.setIsAvailable(true);
        itemDto.setOwner(userDto);
        itemDto.setId(2);

        commentDto = new CommentDto();
        commentDto.setItemId(2);
        commentDto.setAuthorName("author");
        commentDto.setText("text text");
    }

    @Test
    @SneakyThrows
    void getUserTest() {
        int userId = 2;

        /*задаем поведение мока userService*/
        Mockito.when(userService.getUserDtoById(userId))
                .thenReturn(userDto);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        /*контроль вызова нужного метода*/
        Mockito.verify(userService).getUserDtoById(userId);
    }

    @Test
    @SneakyThrows
    void getAllUsersTest() {
        int ownerId = 1;
        int from = 0;
        int size = 2;

        List<UserDto> userDtos = Arrays.asList(userDto, userDto2); //лист с объектами DTO

        /*задаем поведение мока userService*/
        Mockito.when(userService.getAllDto())
                .thenReturn(userDtos);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk()) //проверяем статус 200
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(userService).getAllDto();
        assertThat(mapper.writeValueAsString(userDtos), equalTo(result)); //вернувшийся список совпадает с нужным
    }

    @Test
    @SneakyThrows
    void postUserTest() {
        /*задаем поведение мока userService*/
        Mockito
                .when(userService.createUser(Mockito.any()))
                .thenReturn(userDto);
        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(userService).createUser(Mockito.any());
        assertThat(mapper.writeValueAsString(userDto), equalTo(result)); //вернувщийся userDto совпадает с нужным
    }

    @Test
    @SneakyThrows
    void deleteUserTest() {
        int userId = 2;
        int ownerId = 1;

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk()); //проверяем статус 200

        /*контроль вызова нужного метода*/
        Mockito.verify(userService).deleteUser(userId);
    }

    @Test
    @SneakyThrows
    void putUserTest() {
        int userId = 2;
        /*задаем поведение мока userService*/
        Mockito
                .when(userService.updateUser(Mockito.any(), Mockito.anyInt()))
                .thenReturn(userDto);
        Mockito
                .when(userService.getUserDtoById(Mockito.anyInt()))
                .thenReturn(userDto);
        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(userService).updateUser(Mockito.any(), Mockito.anyInt());
        assertThat(mapper.writeValueAsString(userDto), equalTo(result)); //вернувщийся userDto совпадает с нужным
    }

}
