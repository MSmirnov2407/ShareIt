package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional //чтобы после каждого теста выполнялся RollBack
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class UserServiceIntegrationTest {
    private final UserService userService;
    public UserDto userDto;
    public UserDto userDto2;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setEmail("mail@mail.mail");
        userDto.setName("myName");

        userDto2 = new UserDto();
        userDto2.setEmail("2mail@mail.mail");
        userDto2.setName("2myName");


    }

    @Test
    void createUserTest() {

        /*вызов тестируемого метода */
        UserDto createdUserDto = userService.createUser(userDto);

        /*проверки*/
        assertThat(createdUserDto.getName(), equalTo(userDto.getName())); //имена совпадают
        assertThat(createdUserDto.getEmail(), equalTo(userDto.getEmail())); //email совпадают
    }

    @Test
    void getUserDtoByIdTest() {
        UserDto createdUserDto = userService.createUser(userDto); //создали юзера

        /*вызов тестируемого метода */
        UserDto newUserDto = userService.getUserDtoById(createdUserDto.getId());

        /*проверки*/
        assertThat(newUserDto.getName(), equalTo(createdUserDto.getName())); //имена совпадают
        assertThat(newUserDto.getEmail(), equalTo(createdUserDto.getEmail())); //email совпадают
        assertThat(newUserDto.getId(), equalTo(createdUserDto.getId())); //id совпадают

        assertThrows(ElementNotFoundException.class,
                () -> userService.getUserDtoById(99)); //исключение при неправильном id
    }

    @Test
    void getAllDtoTest() {
        UserDto createdUserDto = userService.createUser(userDto); //создали юзера
        UserDto createdUserDto2 = userService.createUser(userDto2); //создали юзера

        /*вызов тестируемого метода */
        List<UserDto> newUserDtoList = userService.getAllDto();

        /*проверки*/
        assertThat(newUserDtoList.size(), equalTo(2)); //размер совпадает с кол-вом созданных юзеров
        assertThat(newUserDtoList.get(0).getId(), equalTo(createdUserDto.getId())); //id совпадают
        assertThat(newUserDtoList.get(1).getId(), equalTo(createdUserDto2.getId())); //id совпадают
    }

    @Test
    void deleteUserTest() {
        UserDto createdUserDto = userService.createUser(userDto); //создали юзера

        /*вызов тестируемого метода */
        userService.deleteUser(createdUserDto.getId());

        /*проверки*/
        assertThrows(ElementNotFoundException.class,
                () -> userService.getUserDtoById(createdUserDto.getId())); //исключение т.к. элемент удален
    }

    @Test
    void updateItemTest() {
        UserDto createdUserDto = userService.createUser(userDto); //создали юзера
        UserDto createdUserDto2 = userService.createUser(userDto2); //создали юзера

        createdUserDto.setName("updated name"); //изменили имя

        /*вызов тестируемого метода */
        userService.updateUser(createdUserDto, createdUserDto.getId());

        UserDto updatedUserDto = userService.getUserDtoById(createdUserDto.getId()); //взяли из хранилища обнлвленного юзера по id

        /*проверки*/
        assertThat(updatedUserDto.getName(), equalTo("updated name")); //названия совпадают

        assertThrows(ElementNotFoundException.class,
                () -> userService.updateUser(createdUserDto, 99)); //исключение при неправильном id юзера

        createdUserDto.setEmail("2mail@mail.mail"); //изменили на уже существующий
        assertThrows(ElementNotFoundException.class,
                () -> userService.updateUser(createdUserDto, createdUserDto.getId())); //исключение при совпадении email
    }
}
