package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserDto {
    @NotNull
    private int id; //id пользователя
    @Email
    @Pattern(regexp = ".+@.+\\..+")
    private String email; //email
    @NotBlank
    private String name; //имя пользователя
}
