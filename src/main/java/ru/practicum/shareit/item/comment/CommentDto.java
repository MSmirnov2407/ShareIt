package ru.practicum.shareit.item.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDto {

    private int id;
    @NotBlank
    private String text;
    private String authorName;
    private int itemId;
    LocalDateTime created;
}
