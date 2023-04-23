package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository; //хранилище пользователей
    private int id; //id очередного создаваемого пользователя

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * СОздание нового пользователя
     *
     * @param newUser - новый пользователь
     * @return - новый польхователь, взятый из хранилища
     */
    public User createUser(User newUser) {
        validateCreate(newUser); //проверяем данные (помимо валидации аннотациями)
        id++; //для каждого нового пользователя инкрементируем id
        newUser.setId(id); //присваиваем id новому пользователю
        userRepository.saveUser(newUser); //сохраняем пользователя в хранилище
        return newUser;
    }

    /**
     * Получение пользоватля из хранилища по id
     *
     * @param userId
     * @return
     */
    public User getUserById(int userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ElementNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        return user;
    }

    /**
     * Возвращение списка всех пользователей
     * @return
     */
    public List<User> getAll(){
        return userRepository.getAll();
    }

    /**
     * Удаление пользователя из хрангилища
     *
     * @param id
     */
    public void deleteUser(int id) {
        userRepository.deleteUserById(id);
    }

    /**
     * Обновление пользователя в хранилище
     *
     * @param updatedUser
     * @return
     */
    public User updateUser(User updatedUser) {
        validateUpdate(updatedUser); //проверяем данные (помимо валидации аннотациями)
        userRepository.updateUser(updatedUser);
        return userRepository.findUserById(updatedUser.getId());
    }

    /**
     * Валидация данных добавляемого пользователя
     *
     * @param user
     */
    private void validateCreate(User user) {
        Map<Integer, String> usersEmails = userRepository.getUsersEmails(); //запросили из хранилища мапу id-Email
        String email = user.getEmail(); // email обновленного пользователя
        if (usersEmails.containsValue(email)) { //если Email существует - исключение
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
        Map<Integer, String> usersEmails = userRepository.getUsersEmails(); //запросили из хранилища мапу id-Email

        if (!usersEmails.containsKey(updatedUserId)) { //если id нет - исключение
            throw new ElementNotFoundException("Пользователь с id = " + updatedUserId + " не найден.");
        }
        String updatedUserEmail = updatedUser.getEmail(); // email обновленного пользователя
        if (updatedUserEmail == null || updatedUserEmail.isBlank()) { //если в запросе на обновление нет Email, то выходим
            return;
        }
        String oldEmail = usersEmails.get(updatedUserId); //из мапы id-Email взяли старый Email, принадлежащий пользователю
        if (!oldEmail.equals(updatedUserEmail)) { //если при обновлении изменяется Email
            if (usersEmails.containsValue(updatedUserEmail)) { //проверяем, если новый email уже существует - исключение
                throw new EmailAlreadyExistException("Пользователь с Email= " + updatedUserEmail + " уже сущетсвует.");
            }
        }
    }
}
