package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

public interface ItemStorage {

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItems(long userId);

    ItemDto addItem(@Valid ItemDto itemDto, long userId);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    List<ItemDto> searchItems(long userId, String text);
}
