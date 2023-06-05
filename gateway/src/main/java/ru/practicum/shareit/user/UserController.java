package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Get user, userId={}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody UserDto newUserDto) {
        log.info("Creating user {}", newUserDto);
        return userClient.postUser(newUserDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long userId) {
        log.info("Delete user userId={}", userId);
        return userClient.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> putUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        log.info("Update user userId={}", userId);
        return userClient.putUser(userDto, userId);
    }
}
