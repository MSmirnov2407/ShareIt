package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequestDto {
    private int id; //id запроса на вещь
    private String description; //текст запроса
    private UserDto requestor; //пользователь, создавший запрос
    private LocalDateTime created = LocalDateTime.now(); //дата и время сохдания запроса
}
