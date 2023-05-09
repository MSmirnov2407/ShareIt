package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
    private Item item; // id вещи
    private User booker; // пользоавтель , который осуществляет бронирование
    private Status status = Status.WAITING; //статус,подтверждено ли бронирование владельцем

}
