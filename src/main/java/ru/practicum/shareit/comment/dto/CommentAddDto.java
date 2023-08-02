package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentAddDto {
    private long id;
    private String text;
    private long itemId;
    private long authorId;
    private LocalDateTime created;
}
