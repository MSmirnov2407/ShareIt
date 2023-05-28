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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper; //отвечает за преобразование объектов
    @Autowired
    private MockMvc mockMvc; //отвечает за создание запросов
    @MockBean
    private ItemService itemService;

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
    void postItemTest() {
        int ownerId = 1;
        /*задаем поведение мока itemService*/
        Mockito
                .when(itemService.createItem(Mockito.any(), Mockito.anyInt()))
                .thenReturn(itemDto);
        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.id", is(itemDto.getId()), int.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getIsAvailable())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(itemService).createItem(Mockito.any(), Mockito.anyInt());
        assertThat(mapper.writeValueAsString(itemDto), equalTo(result)); //вернувщийся itemDto совпадает с нужным
    }

    @Test
    @SneakyThrows
    void patchItemTest() {
        int itemId = 2;
        int ownerId = 1;
        /*задаем поведение мока itemService*/
        Mockito
                .when(itemService.updateItem(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemDto);
        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.id", is(itemDto.getId()), int.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getIsAvailable())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(itemService).updateItem(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        assertThat(mapper.writeValueAsString(itemDto), equalTo(result)); //вернувщийся itemDto совпадает с нужным
    }

    @Test
    @SneakyThrows
    void getItemTest() {
        int itemId = 2;
        int ownerId = 1;

        /*задаем поведение мока itemService*/
        Mockito.when(itemService.getItemDtoWithBookingsById(itemId, 1))
                .thenReturn(itemDtoWithBookings);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.id", is(itemDtoWithBookings.getId()), int.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBookings.getName())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBookings.getIsAvailable())));

        /*контроль вызова нужного метода*/
        Mockito.verify(itemService).getItemDtoWithBookingsById(itemId, ownerId);
    }

    @Test
    @SneakyThrows
    void getAllItemsByUserTest() {
        int ownerId = 1;
        int from = 0;
        int size = 2;

        List<ItemDtoWithBookings> itemDtoListWithBookings = Arrays.asList(new ItemDtoWithBookings(), new ItemDtoWithBookings()); //лист с пустыми объектами DTO

        /*задаем поведение мока itemService*/
        Mockito.when(itemService.getAllDtoByUser(from, size, ownerId))
                .thenReturn(itemDtoListWithBookings);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk()) //проверяем статус 200
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(itemService).getAllDtoByUser(from, size, ownerId);
        assertThat(mapper.writeValueAsString(itemDtoListWithBookings), equalTo(result)); //вернувщийся itemDto совпадает с нужным
    }

    @Test
    @SneakyThrows
    void searchItemsTest() {
        int ownerId = 1;
        int from = 0;
        int size = 2;
        String text = "search";

        List<ItemDto> itemDtoList = Arrays.asList(new ItemDto(), new ItemDto()); //лист с пустыми объектами DTO

        /*задаем поведение мока itemService*/
        Mockito.when(itemService.searchItemsDto(from, size, text))
                .thenReturn(itemDtoList);

        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(get("/items/search")
                        .queryParam("text", "search")
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk()) //проверяем статус 200
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(itemService).searchItemsDto(from, size, text);
        assertThat(mapper.writeValueAsString(itemDtoList), equalTo(result)); //вернувщийся itemDto совпадает с нужным
    }

    @Test
    @SneakyThrows
    void createCommentTest() {
        int authorId = 1;
        int itemId = 2;
        /*задаем поведение мока itemService*/
        Mockito
                .when(itemService.createComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(commentDto);
        /*формируем запрос и проверяем ожидаемые параметры ответа*/
        /*а также возвращаем содержимое ответа в виде строки*/
        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", authorId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), int.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*контроль вызова нужного метода*/
        Mockito.verify(itemService).createComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
        assertThat(mapper.writeValueAsString(commentDto), equalTo(result)); //вернувщийся itemDto совпадает с нужным
    }
}
