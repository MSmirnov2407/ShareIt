package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class User {
    private int id; //id пользователя
    private String email; //email
    private String name; //имя пользователя

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
