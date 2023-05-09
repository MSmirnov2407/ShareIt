package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    int id; //id запроса на вещь

    @Column(name = "text")
    String requestText; //текст запроса
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requestor; //пользователь, создавший запрос

    @Column(name = "creation_date")
    private LocalDate created = LocalDate.now(); //дата и время сохдания запроса
}
