package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentAddDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader(HEADER) long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(HEADER) long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER) long userId) {
        return ItemMapper.toItemDto(itemService.addItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @PathVariable long itemId, @RequestHeader(HEADER) long userId,
                              @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.updateItem(itemId, userId, itemDto));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(HEADER) long userId,
                                     @RequestParam("text") String text) {
        return itemService.searchItems(userId, text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentAddDto comment, @RequestHeader(HEADER) long userId,
                                 @PathVariable long itemId) {
        return CommentMapper.toCommentDto(itemService.addComment(comment, userId, itemId));
    }
}
