package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    int id; //id запроса на вещь
    @NotBlank
    String requestText; //текст запроса
    @NotNull
    private User requestor; //пользователь, создавший запрос
    @NotNull
    private LocalDate created; //дата и время сохдания запроса
}
