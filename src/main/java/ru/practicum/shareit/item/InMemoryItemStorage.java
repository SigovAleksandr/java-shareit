package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    UserStorage userStorage;
    private final HashMap<Long, Item> items = new HashMap<>();
    private long id = 0;

    private long getId() {
        id = id + 1;
        return id;
    }

    @Autowired
    public InMemoryItemStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        if (!items.containsKey(itemId)) {
            throw new ResourceNotFoundException("Item with this id not exist");
        }
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        List<ItemDto> list = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                list.add(ItemMapper.toItemDto(item));
            }
        }
        return list;
    }

    @Override
    public ItemDto addItem(Item item, long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new ResourceNotFoundException("User with this id not exist");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Parameter available must not be null");
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Name must not be null or empty");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Description must not be null or empty");
        }
        item.setId(getId());
        item.setOwner(UserMapper.fromUserDto(userStorage.getUserById(userId)));
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new ResourceNotFoundException("Item with this id not exist");
        }
        Item newItem = items.get(itemId);
        if (newItem.getOwner().getId() != userId) {
            throw new ResourceNotFoundException("This userId is not an owner of item");
        }
        if (item.getName() != null)
            newItem.setName(item.getName());
        if (item.getDescription() != null)
            newItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            newItem.setAvailable(item.getAvailable());
        items.put(itemId, newItem);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        List<ItemDto> found = new ArrayList<>();
        if (!text.isBlank() || !text.isEmpty()) {
            String str = text.toLowerCase();
            for (Item item : items.values()) {
                if (item.getName().toLowerCase().contains(str)
                        || item.getDescription().toLowerCase().contains(str)
                        && item.getAvailable()) {
                    found.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return found;
    }
}
