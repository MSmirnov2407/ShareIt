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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItem;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutBooker;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper; //отвечает за преобразование объектов
    @Autowired
    private MockMvc mockMvc; //отвечает за создание запросов
    @MockBean
    private BookingService bookingService;

    public UserDto userDto;
    public UserDto userDto2;

    public ItemDtoWithBookings itemDtoWithBookings;
    public ItemDto itemDto;
    public CommentDto commentDto;
    private BookingDto bookingDto;
    private BookingDtoWithItem bookingDtoWithItem;


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

        bookingDto = new BookingDto();
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setId(33);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));

        bookingDtoWithItem = new BookingDtoWithItem();
        bookingDtoWithItem.setStatus(Status.WAITING);
        bookingDtoWithItem.setId(33);
        bookingDtoWithItem.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDtoWithItem.setEnd(LocalDateTime.now().plusSeconds(2));
        bookingDtoWithItem.setItem(itemDto);
    }

    @Test
    @SneakyThrows
    void postBookingTest() {
        int ownerId = 1;
        /*задаем поведение мока bookingService*/
        Mockito
                .when(bookingService.createBooking(Mockito.any(), Mockito.anyInt()))
                .thenReturn(bookingDtoWithItem);
        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.id", is(bookingDtoWithItem.getId())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(bookingService).createBooking(Mockito.any(), Mockito.anyInt());
        assertThat(mapper.writeValueAsString(bookingDtoWithItem), equalTo(result)); //вернувщийся itemDto совпадает с нужным
    }

    @Test
    @SneakyThrows
    void patchConfirmationTest() {
        int bookingId = 2;
        int userId = 1;
        boolean isApproved = true;
        /*задаем поведение мока bookingService*/
        Mockito
                .when(bookingService.updateBooking(bookingId, isApproved, userId))
                .thenReturn(bookingDtoWithItem);
        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.id", is(bookingDtoWithItem.getId())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(bookingService).updateBooking(bookingId, isApproved, userId);
        assertThat(mapper.writeValueAsString(bookingDtoWithItem), equalTo(result)); //вернувщийся itemDto совпадает с нужным
    }

    @Test
    @SneakyThrows
    void getBookingTest() {
        int bookingId = 2;
        int userId = 1;

        /*задаем поведение мока bookingService*/
        Mockito.when(bookingService.getBookingDtoById(bookingId, userId))
                .thenReturn(bookingDtoWithItem);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.id", is(bookingDtoWithItem.getId())));

        /*контроль вызова нужного метода*/
        Mockito.verify(bookingService).getBookingDtoById(bookingId, userId);
    }

    @Test
    @SneakyThrows
    void getAllByUserTest() {
        int userId = 1;
        int from = 0;
        int size = 2;
        String state = "WAITING";

        List<BookingDtoWithItem> bookingDtoWithItemList = Arrays.asList(new BookingDtoWithItem(), new BookingDtoWithItem()); //лист с пустыми объектами DTO

        /*задаем поведение мока bookingService*/
        Mockito.when(bookingService.getAllDtoByUserAndState(from, size, userId, state))
                .thenReturn(bookingDtoWithItemList);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("state", state)
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk()) //проверяем статус 200
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(bookingService).getAllDtoByUserAndState(from, size, userId, state);
        assertThat(mapper.writeValueAsString(bookingDtoWithItemList), equalTo(result)); //вернувщийся itemDto совпадает с нужным
    }

    @Test
    @SneakyThrows
    void getAllByItemTest() {
        int userId = 1;
        int from = 0;
        int size = 2;
        String state = "WAITING";

        List<BookingDtoWithItem> bookingDtoWithItemList = Arrays.asList(new BookingDtoWithItem(), new BookingDtoWithItem()); //лист с пустыми объектами DTO

        /*задаем поведение мока bookingService*/
        Mockito.when(bookingService.getAllDtoByOwnerAndState(from, size, userId, state))
                .thenReturn(bookingDtoWithItemList);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("state", state)
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk()) //проверяем статус 200
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(bookingService).getAllDtoByOwnerAndState(from, size, userId, state);
        assertThat(mapper.writeValueAsString(bookingDtoWithItemList), equalTo(result)); //вернувщийся itemDto совпадает с нужным
    }
}
