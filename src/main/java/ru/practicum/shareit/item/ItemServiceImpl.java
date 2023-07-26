package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Item> getItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResourceNotFoundException("Item not found"));
        if (item.getOwnerId() != userId) {
            return Optional.of(item);
        }
        return Optional.of(item);
    }

    @Override
    public List<Item> getItems(long userId) {
        List<Item> itemList = itemRepository.findItemsByOwnerId(userId);
        return itemList;
    }

    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.fromItemDto(itemDto);
        if (item.getAvailable() == null) {
            throw new ValidationException("Available must not be null");
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Name must not be null");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Description must not be null");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        item.setOwnerId(userId);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(long itemId, long userId, ItemDto itemDto) {
        Item newItem = itemRepository.findById(itemId).orElseThrow(() ->
                new ResourceNotFoundException("Item not found"));
        if (newItem.getOwnerId() != userId) {
            throw new ResourceNotFoundException("Invalid owner id");
        }
        Item item = ItemMapper.fromItemDto(itemDto);
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        return itemRepository.save(newItem);
    }

    @Override
    public List<Item> searchItems(long userId, String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        String query = text.toUpperCase();
        return itemRepository.search(query);
    }
}
