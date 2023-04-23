package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private int id; //id пользователя
    @NotNull
    @Email
    @Pattern(regexp = ".+@.+\\..+")
    private String email; //email

    @NotBlank
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
