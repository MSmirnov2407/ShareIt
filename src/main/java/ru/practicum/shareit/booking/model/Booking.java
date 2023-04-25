package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
public class Booking {
    private int id; //номер бронирования
    private LocalDate start; // дата начала аренды
    private LocalDate end; // дата окончания аренды
    private Item item; // вещи
    private User booker; //пользователь, который осуществляет бронирование
    private Status status; //статус,подтверждено ли бронирование владельцем
}
