package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingDtoWithItem {
    private int id; //номер бронирования
    @NotNull
    private LocalDateTime start; // дата начала аренды
    @NotNull
    private LocalDateTime end; // дата окончания аренды
    private ItemDto item; // id вещи
    private UserDto booker; // пользоавтель , который осуществляет бронирование
    private Status status = Status.WAITING; //статус,подтверждено ли бронирование владельцем

}
