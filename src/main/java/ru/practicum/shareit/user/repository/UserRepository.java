package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    /**
     * Получение User из хранилища его id
     *
     * @param id - id пользователя
     * @return User - пользователь
     */
    User findUserById(int id);

    /**
     * Получение списка всех пользователей
     *
     * @return
     */
    List<User> getAll();

    /**
     * Сохранение нового пользователя в хранилище
     *
     * @param newUser сохраняемый пользователь
     * @return сохраненный пользователь
     */
    User saveUser(User newUser);

    /**
     * Обновление пользователя в хранилище
     *
     * @param updatedUser - обновленный пользователь
     * @return обновленный пользователь
     */
    User updateUser(User updatedUser);

    /**
     * УДаление пользовальля из хранилища
     *
     * @param id - id пользователя
     */
    void deleteUserById(int id);

    /**
     * Возращает мапу из id-емейлов пользователей
     *
     * @return сет из емейлов
     */
    Map<Integer, String> getUsersEmails();
}
