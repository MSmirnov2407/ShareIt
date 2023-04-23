package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        log.info("Запрошен Пользователь. Id = {}", userId);
        return UserMapper.userToDto(userService.getUserById(userId)); //возвращаем dto объекта, взятого по id
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен список пользователей");
        return userService.getAll().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList()); //вернули список с преобразованием userToDto
    }

    @PostMapping
    public UserDto postUser(@Valid @RequestBody User newUser) {
        User user = userService.createUser(newUser);
        log.info("Создан Пользователь. Id = {}, email = {}", user.getId(), user.getEmail());
        return UserMapper.userToDto(user); //возвращаем dto созданного объекта
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        log.info("Удален Пользователь. Id = {}", userId);
    }

    @PatchMapping("/{userId}")
    public UserDto putUser(@RequestBody User user, @PathVariable int userId) {
        user.setId(userId);
        userService.updateUser(user);
        log.info("Обновлен Пользователь. Id = {}", user.getId());
        return UserMapper.userToDto(userService.getUserById(userId)); //возвращаем dto объекта, взятого по id
    }
}
