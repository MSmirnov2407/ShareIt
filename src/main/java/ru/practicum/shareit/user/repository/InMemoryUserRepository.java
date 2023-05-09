package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>(); //мапа id-пользователь

    @Override
    public Optional<User> findById(int id) {
        return Optional.of(users.get(id)); //вернули пользователя по id
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<User>(users.values()); //возвращаем значения мапы в виде списка
    }

    @Override
    public User save(User newUser) {
        Integer id = newUser.getId(); //взяли id пользователя
        users.put(id, newUser); //по id сложили в мапу нового пользователя
        return users.get(id); //вернули пользователя по id
    }

    @Override
    public User updateUser(User updatedUser) {
        Integer id = updatedUser.getId(); //взяли id пользователя
        User user = users.get(id); //взяли из хранилища пользователя по этому id
        String email = updatedUser.getEmail();
        String name = updatedUser.getName();
        /*обновление данных в пользователе, при их наличии в updatedUser*/
        if (!(email == null || email.isBlank())) {
            user.setEmail(updatedUser.getEmail());
        }
        if (!(name == null || name.isBlank())) {
            user.setName(updatedUser.getName());
        }

        users.put(id, user); //по id сложили в мапу обновленного пользователя
        return users.get(id); //вернули пользователя по id
    }

    @Override
    public void deleteById(int id) {
        users.remove(id);
    }

    @Override
    public Map<Integer, String> getUsersEmails() {
        return users.values().stream()
                .collect(Collectors.toMap(User::getId, User::getEmail));
    }
}
