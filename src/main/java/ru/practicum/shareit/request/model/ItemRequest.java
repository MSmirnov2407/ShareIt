package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
public class ItemRequest {
    int id; //id запроса на вещь
    String requestText; //текст запроса
    private User requestor; //пользователь, создавший запрос
    private LocalDate created; //дата и время сохдания запроса
}
