package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment fromCommentDto(CommentAddDto commentAddDto) {
        return new Comment(
                0,
                commentAddDto.getText(),
                0,
                null,
                null
        );
    }
}
