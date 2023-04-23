package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id; //номер бронирования
    @NotNull
    private LocalDate start; // дата начала аренды
    @NotNull
    private LocalDate end; // дата окончания аренды
    @NotNull
    private Item item; // вещи
    @NotNull
    private User booker; //пользователь, который осуществляет бронирование
    @NotNull
    private Status status; //статус,подтверждено ли бронирование владельцем
}
