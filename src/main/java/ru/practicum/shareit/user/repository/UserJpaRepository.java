package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserJpaRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u.email " +
            "FROM User AS u")
    List<String> getUsersEmails();

}
