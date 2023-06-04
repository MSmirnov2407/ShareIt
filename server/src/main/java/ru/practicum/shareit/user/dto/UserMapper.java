package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    /**
     * Метод преобразования объекта в его Dto
     *
     * @param user - объект User
     * @return - Dto объекта User
     */
    public static UserDto userToDto(User user) {
        UserDto userDto = new UserDto(); //создали объект Dto

        userDto.setId(user.getId()); // установили id
        userDto.setEmail(user.getEmail()); //установили Email
        userDto.setName(user.getName()); //установили Name

        return userDto; //вернули Dto
    }

    /**
     * Обратное преобразование DTO В объект
     *
     * @param userDto - DTO user
     * @return User
     */
    public static User dtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User(); //создали объект User

        user.setId(userDto.getId()); // установили id
        user.setEmail(userDto.getEmail()); //установили Email
        user.setName(userDto.getName()); //установили Name

        return user; //вернули User
    }
}
