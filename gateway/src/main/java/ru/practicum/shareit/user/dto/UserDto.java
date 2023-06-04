package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserDto {
    @NotNull
    private int id; //id пользователя
    @Email
    @Pattern(regexp = ".+@.+\\..+")
    @NotNull
    private String email; //email
    @NotBlank
    private String name; //имя пользователя
}
