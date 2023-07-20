package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final UserStorage userStorage;
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    private long getId() {
        id = id + 1;
        return id;
    }

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
    public ItemDto addItem(@Valid ItemDto itemDto, long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new ResourceNotFoundException("User with this id not exist");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Parameter available must not be null");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("Name must not be null or empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Description must not be null or empty");
        }
        itemDto.setId(getId());
        itemDto.setOwner(UserMapper.fromUserDto(userStorage.getUserById(userId)));
        items.put(itemDto.getId(), ItemMapper.fromItemDto(itemDto));
        return itemDto;
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        if (!items.containsKey(itemId)) {
            throw new ResourceNotFoundException("Item with this id not exist");
        }
        Item newItem = items.get(itemId);
        if (newItem.getOwner().getId() != userId) {
            throw new ResourceNotFoundException("This userId is not an owner of item");
        }
        if (itemDto.getName() != null)
            newItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            newItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            newItem.setAvailable(itemDto.getAvailable());
        items.put(itemId, newItem);
        return ItemMapper.toItemDto(newItem);
    }

    public List<ItemDto> searchItems(long userId, String text) {
        List<ItemDto> found = new ArrayList<>();
        if (!text.isBlank() || !text.isEmpty()) {
            String str = text.toLowerCase();
            found = items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(str)
                            || item.getDescription().toLowerCase().contains(str))
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return found;
    }
}
