package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    /**
     * Получение User из хранилища его id
     *
     * @param id - id пользователя
     * @return User - пользователь
     */
    Optional<User> findById(int id);

    /**
     * Получение списка всех пользователей
     *
     * @return
     */
    List<User> findAll();

    /**
     * Сохранение нового пользователя в хранилище
     *
     * @param newUser сохраняемый пользователь
     * @return сохраненный пользователь
     */
    User save(User newUser);

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
    void deleteById(int id);

    /**
     * Возращает мапу из id-емейлов пользователей
     *
     * @return сет из емейлов
     */
    Map<Integer, String> getUsersEmails();
}
