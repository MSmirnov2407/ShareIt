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
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper; //отвечает за преобразование объектов
    @Autowired
    private MockMvc mockMvc; //отвечает за создание запросов
    @MockBean
    private RequestService requestService;

    public UserDto userDto;
    public UserDto userDto2;

    public ItemDtoWithBookings itemDtoWithBookings;
    public ItemDto itemDto;
    public CommentDto commentDto;
    public ItemRequestDto itemRequestDto;
    private ItemRequestDtoWithAnswer itemRequestDtoWithAnswer;

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

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("descr");

        itemRequestDtoWithAnswer = new ItemRequestDtoWithAnswer();
        itemRequestDtoWithAnswer.setDescription("dddd");
    }

    @Test
    @SneakyThrows
    void postRequestTest() {
        int userId = 1;
        /*задаем поведение мока requestService*/
        Mockito
                .when(requestService.createRequest(Mockito.any(), Mockito.anyInt()))
                .thenReturn(itemRequestDto);
        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(requestService).createRequest(Mockito.any(), Mockito.anyInt());
        assertThat(mapper.writeValueAsString(itemRequestDto), equalTo(result)); //вернувщийся itemRequestDto совпадает с нужным
    }

    @Test
    @SneakyThrows
    void getRequestTest() {
        int userId = 1;

        List<ItemRequestDtoWithAnswer> requestList = Arrays.asList(new ItemRequestDtoWithAnswer(), new ItemRequestDtoWithAnswer()); //лист с пустыми объектами DTO

        /*задаем поведение мока requestService*/
        Mockito.when(requestService.getAllDtoWithAnswByUserId(userId))
                .thenReturn(requestList);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk()) //проверяем статус 200
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(requestService).getAllDtoWithAnswByUserId(userId);
        assertThat(mapper.writeValueAsString(requestList), equalTo(result)); //вернувщийся requestList совпадает с нужным
    }

    @Test
    @SneakyThrows
    void getAllRequestsTest() {
        int userId = 1;
        int from = 0;
        int size = 2;

        List<ItemRequestDtoWithAnswer> requestList = Arrays.asList(new ItemRequestDtoWithAnswer(), new ItemRequestDtoWithAnswer()); //лист с пустыми объектами DTO

        /*задаем поведение мока requestService*/
        Mockito.when(requestService.getAllDtoWithAnsw(from, size, userId))
                .thenReturn(requestList);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk()) //проверяем статус 200
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(requestService).getAllDtoWithAnsw(from, size, userId);
        assertThat(mapper.writeValueAsString(requestList), equalTo(result)); //вернувщийся requestList совпадает с нужным
    }

    @Test
    @SneakyThrows
    void getRequestByIdTest() {
        int requestId = 2;
        int userId = 1;

        /*задаем поведение мока requestService*/
        Mockito.when(requestService.getDtoWithAnswById(requestId, userId))
                .thenReturn(itemRequestDtoWithAnswer);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.description", is(itemRequestDtoWithAnswer.getDescription())));

        /*контроль вызова нужного метода*/
        Mockito.verify(requestService).getDtoWithAnswById(requestId, userId);
    }
}
