package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

@Component
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(long itemId);
}
