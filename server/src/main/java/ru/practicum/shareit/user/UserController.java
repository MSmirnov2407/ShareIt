package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        log.info("Запрошен Пользователь. Id = {}", userId);
        return userService.getUserDtoById(userId); //возвращаем dto объекта, взятого по id
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен список пользователей");
        return userService.getAllDto();
    }

    @PostMapping
    public UserDto postUser(@Valid @RequestBody UserDto newUserDto) {
        UserDto userDto = userService.createUser(newUserDto);
        log.info("Создан Пользователь. Id = {}, email = {}", userDto.getId(), userDto.getEmail());
        return userDto; //возвращаем dto созданного объекта
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        log.info("Удален Пользователь. Id = {}", userId);
    }

    @PatchMapping("/{userId}")
    public UserDto putUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        userService.updateUser(userDto, userId);
        log.info("Обновлен Пользователь. Id = {}", userDto.getId());
        return userService.getUserDtoById(userId); //возвращаем dto объекта, взятого по id
    }
}
