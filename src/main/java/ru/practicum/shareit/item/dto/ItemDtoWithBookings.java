package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutBooker;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemDtoWithBookings {

    private int id; //id вещи
    private User owner; //владелец вещи
    @NotBlank
    private String name; //наименование
    @NotBlank
    private String description; //описание
    @NotNull
    @JsonProperty("available")
    private Boolean isAvailable; //доступность вещи для аренды
    private ItemRequest request; //запрос, по которому была создана вещь

    private BookingDtoWithoutBooker lastBooking; //последнее бронирование
    private BookingDtoWithoutBooker nextBooking; //ближайшее следующее бронирование

    private List<CommentDto> comments;
}
