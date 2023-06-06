package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
public class ItemRequestDto {
    private int id; //id запроса на вещь
    @NotBlank
    private String description; //текст запроса
    private UserDto requestor; //пользователь, создавший запрос
    private LocalDateTime created = LocalDateTime.now(); //дата и время создания запроса
}
