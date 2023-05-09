package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserJpaRepository userRepository; //хранилище пользователей

    @Autowired
    public UserService(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * СОздание нового пользователя
     *
     * @param newUserDto - новый пользователь в виде DTO
     * @return - новый польхователь, взятый из хранилища, в виде DTO
     */
    public UserDto createUser(UserDto newUserDto) {
        User newUser = UserMapper.dtoToUser(newUserDto); //преобразовали DTO в объект
        userRepository.save(newUser); //сохраняем пользователя в хранилище
        return UserMapper.userToDto(newUser);
    }

    /**
     * Получение пользоватля из хранилища по id
     *
     * @param userId
     * @return
     */
    public UserDto getUserDtoById(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ElementNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        return UserMapper.userToDto(user.get()); //возаращаем DTO объекта
    }

    /**
     * Возвращение списка всех пользователей
     *
     * @return
     */
    public List<UserDto> getAllDto() {
        return userRepository.findAll().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList()); //вернули список с преобразованием userToDto
    }

    /**
     * Удаление пользователя из хранилища
     *
     * @param id
     */
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    /**
     * Обновление пользователя в хранилище
     *
     * @param updatedUserDto
     * @param userId
     * @return
     */
    public UserDto updateUser(UserDto updatedUserDto, int userId) {
        updatedUserDto.setId(userId); //Установлили id
        User updatedUser = UserMapper.dtoToUser(updatedUserDto); //преобразовали DTO В объект
        validateUpdate(updatedUser); //проверяем данные (помимо валидации аннотациями)

        Integer updatedUserId = updatedUser.getId(); //взяли id пользователя
        User user = userRepository.findById(updatedUserId).get(); //взяли из хранилища пользователя по этому id
        String email = updatedUser.getEmail();
        String name = updatedUser.getName();
        /*обновление данных в пользователе, при их наличии в updatedUser*/
        if (!(email == null || email.isBlank())) {
            user.setEmail(updatedUser.getEmail());
        }
        if (!(name == null || name.isBlank())) {
            user.setName(updatedUser.getName());
        }
        userRepository.save(user);
        return UserMapper.userToDto(userRepository.findById(updatedUserId).get()); //возаращаем DTO объекта
    }

    /**
     * Валидация данных добавляемого пользователя
     *
     * @param user
     */
    private void validateCreate(User user) {
        List<String> usersEmails = userRepository.getUsersEmails(); //запросили из хранилища список id-Email
        log.warn("email существующих пользователей " + usersEmails);
        String email = user.getEmail(); // email обновленного пользователя
        if (usersEmails.contains(email)) { //если Email существует - исключение
            throw new EmailAlreadyExistException("UserService Пользователь с Email= " + email + " уже сущетсвует.");
        }
    }

    /**
     * Валидация данных Обновляемого пользователя
     *
     * @param updatedUser
     */
    private void validateUpdate(User updatedUser) {
        int updatedUserId = updatedUser.getId(); // id обновленного пользователя
        List<User> users = userRepository.findAll(); //запросили из хранилища всех пользователей

        Map<Integer, String> usersEmails = users.stream()
                .collect(Collectors.toMap(User::getId, User::getEmail)); //создаем мапу id-email

        if (!usersEmails.containsKey(updatedUserId)) { //если юзера нет - исключение
            throw new ElementNotFoundException("Пользователь с id = " + updatedUserId + " не найден.");
        }
        String updatedUserEmail = updatedUser.getEmail(); // email обновленного пользователя
        if (updatedUserEmail == null || updatedUserEmail.isBlank()) { //если в запросе на обновление нет Email, то выходим
            return;
        }
        String oldEmail = usersEmails.get(updatedUserId); //взяли старый Email, принадлежащий пользователю

        if (!oldEmail.equals(updatedUserEmail)) { //если при обновлении изменяется Email
            if (usersEmails.containsValue(updatedUserEmail)) { //проверяем, если новый email уже существует - исключение
                throw new EmailAlreadyExistException("Пользователь с Email= " + updatedUserEmail + " уже сущетсвует.");
            }
        }
    }
}
