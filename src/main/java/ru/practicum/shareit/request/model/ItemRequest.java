package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id запроса на вещь

    @Column(name = "text")
    private String description; //текст запроса
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User requestor; //пользователь, создавший запрос

    @Column(name = "creation_date")
    private LocalDateTime created = LocalDateTime.now(); //дата и время сохдания запроса
}
