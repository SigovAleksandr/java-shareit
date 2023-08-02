package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentAddDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResourceNotFoundException("Item not found"));
        List<Booking> lastBookingList = bookingRepository.findLastBooking(itemId);
        List<Booking> nextBookingList = bookingRepository.findNextBooking(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(findItemComments(itemId));
        if (item.getOwnerId() != userId) {
            return itemDto;
        }
        if (!lastBookingList.isEmpty()) {
            if (lastBookingList.size() > 1) {
                Booking booking = lastBookingList.get(0);
                for (int i = 1; i < lastBookingList.size(); i++) {
                    if (lastBookingList.get(i).getStart().compareTo(booking.getStart()) > 0) {
                        booking = lastBookingList.get(i);
                    }
                }
                itemDto.setLastBooking(BookingMapper.toBookingDto(booking));
            } else {
                itemDto.setLastBooking(BookingMapper.toBookingDto(lastBookingList.get(0)));
            }
        }
        if (!nextBookingList.isEmpty()) {
            if (!itemDto.getLastBooking().equals(BookingMapper.toBookingDto(nextBookingList.get(0)))) {
                itemDto.setNextBooking(BookingMapper.toBookingDto(nextBookingList.get(0)));
            }
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> itemList = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);
        boolean isOwner = true;
        List<Booking> bookingList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getOwnerId() != userId) {
                isOwner = false;
            }
            bookingList.addAll(bookingRepository.findByItemIdOrderByStartDesc(item.getId()));
        }
        if (!isOwner) {
            throw new ResourceNotFoundException("User is not an owner of item");
        }
        List<ItemDto> dtoList = itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        for (Booking booking : bookingList) {
            for (ItemDto itemDto : dtoList) {
                if (booking.getItem().getId() == itemDto.getId()) {
                    itemDto.setNextBooking(BookingMapper.toBookingDto(bookingList.get(bookingList.size() - 2)));
                    itemDto.setLastBooking(BookingMapper.toBookingDto(bookingList.get(bookingList.size() - 1)));
                }
            }
        }
        return dtoList;
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

    @Override
    public Comment addComment(CommentAddDto comment, long userId, long itemId) {
        Comment addComment = CommentMapper.fromCommentDto(comment);
        if (comment.getText().isEmpty()) {
            throw new ValidationException("Comment must not be empty");
        }
        List<Booking> bookingList = bookingRepository.findByBookerIdAndItemId(userId, itemId);
        if (bookingList.isEmpty()) {
            throw new ValidationException("This user is not the booker for item");
        }
        if (bookingList.get(0).getStart().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Start time of booking in the future");
        }
        addComment.setItemId(itemId);
        addComment.setAuthor(userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found")));
        addComment.setCreated(LocalDateTime.now());
        return commentRepository.save(addComment);
    }

    @Override
    public List<CommentDto> findItemComments(long itemId) {
        List<Comment> commentsList = commentRepository.findByItemId(itemId);
        return commentsList.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }


}
