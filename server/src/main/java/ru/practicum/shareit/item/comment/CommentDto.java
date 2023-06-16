package ru.practicum.shareit.item.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDto {

    private int id;
    private String text;
    private String authorName;
    private int itemId;
    LocalDateTime created;
}
