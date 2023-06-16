package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private int id; //id пользователя
    private String email; //email
    private String name; //имя пользователя
}
