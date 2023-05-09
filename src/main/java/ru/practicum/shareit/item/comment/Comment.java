package ru.practicum.shareit.item.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id комментария
    @Column(name = "text")
    private String text; //текст комментария
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item; //вещь, к которой относится комментарий
    @OneToOne
    @JoinColumn(name = "author_id")
    private User author; //автор комментария
    @Column(name = "created")
    private LocalDateTime created; //время создания комментария
}
