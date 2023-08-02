package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentAddDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItems(long userId);

    Item addItem(@Valid ItemDto itemDto, long userId);

    Item updateItem(long itemId, long userId, ItemDto itemDto);

    List<Item> searchItems(long userId, String text);

    Comment addComment(CommentAddDto comment, long userId, long itemId);

    List<CommentDto> findItemComments(long itemId);
}
