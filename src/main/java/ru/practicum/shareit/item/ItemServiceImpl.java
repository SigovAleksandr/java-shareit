package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class ItemServiceImpl implements ItemService {

    ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        return itemStorage.getItemById(itemId, userId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemStorage.getItems(userId);
    }

    @Override
    public ItemDto addItem(Item item, long userId) {
        return itemStorage.addItem(item, userId);
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, Item item) {
        return itemStorage.updateItem(itemId, userId, item);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        return itemStorage.searchItems(userId, text);
    }
}
