package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface ItemService {

    Optional<Item> getItemById(long itemId, long userId);

    List<Item> getItems(long userId);

    Item addItem(@Valid ItemDto itemDto, long userId);

    Item updateItem(long itemId, long userId, ItemDto itemDto);

    List<Item> searchItems(long userId, String text);
}
