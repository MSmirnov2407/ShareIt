package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    public UserDto userDto;
    public UserDto userDto2;
    public UserDto userDto3;
    public User user;
    public List<UserDto> userDtoList;
    public List<User> userList;
    @Mock
    static UserJpaRepository mockUserJpaRepository;

    @InjectMocks
    public UserService userService;

    @BeforeEach
    void setUp() {
        /*создаем тестовые объекты*/
        userDto = new UserDto();
        userDto.setId(11);
        userDto.setEmail("11mail@mail.mail");
        userDto.setName("11myName");

        userDto2 = new UserDto();
        userDto2.setId(22);
        userDto2.setEmail("22mail@mail.mail");
        userDto2.setName("22myName");

        userDto3 = new UserDto();
        userDto3.setId(33);
        userDto3.setEmail("33mail@mail.mail");
        userDto3.setName("33myName");

        user = new User();
        user.setId(33);
        user.setEmail("33mail@mail.mail");
        user.setName("44myName");

        userDtoList = Arrays.asList(userDto, userDto2, userDto3);
        userList = userDtoList.stream()
                .map(UserMapper::dtoToUser)
                .collect(Collectors.toList());
    }

    @Test
    void createUserTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserJpaRepository.save(Mockito.any()))
                .thenReturn(UserMapper.dtoToUser(userDto)); //при сохранении пользователя, возвращаем пользователя

        /*вызываем тестируемый метод. Передаем созданный userDto*/
        UserDto newUserDto = userService.createUser(userDto);

        /*проверки*/
        assertThat(newUserDto.getId(), equalTo(userDto.getId())); //id совпадает с переданным, т.к. сохранение в базу не происходит и новый id не присваивается
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail())); //email сохраненного поьлзователя совпадает с переданным
    }

    @Test
    void getUserDtoByIdTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserJpaRepository.findById(11))
                .thenReturn(Optional.of(UserMapper.dtoToUser(userDto))); //при сохранении пользователя, возвращаем Optional пользователя

        /*вызываем тестируемый метод. Передаем id пользователя*/
        UserDto newUserDto = userService.getUserDtoById(11);

        /*проверки*/
        assertThat(newUserDto.getId(), equalTo(userDto.getId())); //id совпадает с переданным, т.к. сохранение в базу не происходит и новый id не присваивается
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail())); //email сохраненного поьлзователя совпадает с переданным
    }

    @Test
    void getAllDtoTest() {
        /*задаем поведение моков, имитирующих классы, используемые в тестируемом методе*/
        Mockito
                .when(mockUserJpaRepository.findAll())
                .thenReturn(userList); //при сохранении пользователя, возвращаем Optional пользователя

        /*вызываем тестируемый метод.*/
        List<UserDto> newUserDtolist = userService.getAllDto();

        /*проверки*/
        assertThat(newUserDtolist.size(), equalTo(userList.size())); //размер списка совпадает с переданным
        assertThat(newUserDtolist.get(0).getId(), equalTo(userList.get(0).getId())); //id элементов совпадают
        assertThat(newUserDtolist.get(1).getId(), equalTo(userList.get(1).getId())); //id элементов совпадают
        assertThat(newUserDtolist.get(2).getId(), equalTo(userList.get(2).getId())); //id элементов совпадают
    }

    @Test
    void updateUserTest() {

        Mockito
                .when(mockUserJpaRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user)); //при запросе User по id, возвращаем Optional созданного User
        Mockito
                .when(mockUserJpaRepository.save(Mockito.any()))
                .thenReturn(user); //при сохранении пользователя , возвращаем просто его же
        Mockito
                .when(mockUserJpaRepository.findAll())
                .thenReturn(userList); //при сохранении пользователя , возвращаем просто его же

        /*вызываем тестируемый метод*/
        UserDto updatedUserDto = userService.updateUser(userDto3, 33);

        /*проверяем количество вызовов методов*/
        Mockito.verify(mockUserJpaRepository, Mockito.times(2))
                .findById(user.getId());
        Mockito.verify(mockUserJpaRepository, Mockito.times(1))
                .save(Mockito.any());

        assertThat(updatedUserDto.getId(), equalTo(user.getId()));
    }
}
