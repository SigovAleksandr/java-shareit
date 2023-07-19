package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    ItemDto getItemById(long itemId, long userId);
    List<ItemDto> getItems(long userId);
    ItemDto addItem(Item item, long userId);
    ItemDto updateItem(long itemId, long userId, Item item);
    List<ItemDto> searchItems(long userId, String text);
}
