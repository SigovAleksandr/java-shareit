package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

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
    public ItemDto addItem(@Valid ItemDto itemDto, long userId) {
        return itemStorage.addItem(itemDto, userId);
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        return itemStorage.updateItem(itemId, userId, itemDto);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        return itemStorage.searchItems(userId, text);
    }
}
