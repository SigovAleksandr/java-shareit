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

//    public static Comment fromCommentDto(CommentDto commentDto) {
//        return new Comment(
//                commentDto.getId(),
//                commentDto.getText(),
//                commentDto.getItemId(),
//                commentDto.ge
//        )
//    }
}
