package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentAddDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItems(long userId);

    ItemDto addItem(@Valid ItemDto itemDto, long userId);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    List<ItemDto> searchItems(long userId, String text);

    CommentDto addComment(CommentAddDto comment, long userId, long itemId);

    List<CommentDto> findItemComments(long itemId);
}
