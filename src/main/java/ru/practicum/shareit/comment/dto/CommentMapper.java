package ru.practicum.shareit.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.model.Comment;

@UtilityClass
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItemId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment fromCommentDto(CommentAddDto commentAddDto) {
        return Comment.builder()
                .id(0)
                .text(commentAddDto.getText())
                .itemId(0)
                .author(null)
                .created(null)
                .build();
    }
}
