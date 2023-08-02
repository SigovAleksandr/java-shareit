package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Booking getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found"));
        if (userId == booking.getBooker().getId() ||
                userId == itemRepository.findById(booking.getItem().getId()).orElseThrow().getOwnerId()) {
            return booking;
        } else {
            throw new ResourceNotFoundException("Invalid user");
        }
    }

    @Override
    public Booking addBooking(BookingAddDto booking, long userId) {
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() ->
                new ResourceNotFoundException("Item not found"));
        Booking bookingAdd = BookingMapper.toBookingFromAddDto(booking);
        if (!item.getAvailable()) {
            throw new ValidationException("This item is unavailable");
        } else if (booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(booking.getStart()) ||
                booking.getStart().equals(booking.getEnd())) {
            throw new ValidationException("Incorrect start or end date");
        } else if (booker.getId() == item.getOwnerId()) {
            throw new ResourceNotFoundException("Booking items from owner is unavailable");
        }
        bookingAdd.setBooker(booker);
        bookingAdd.setStatus(BookingStatus.WAITING);
        bookingAdd.setItem(item);
        return bookingRepository.save(bookingAdd);
    }

    @Override
    public Booking approveBooking(long bookingId, long userId, boolean approve) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found"));
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Booking already approved");
        }
        if (itemRepository.findById(booking.getItem().getId()).orElseThrow().getOwnerId() != userId) {
            throw new ResourceNotFoundException("User with this id is not an owner");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getUserBooking(String state, long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        switch (state) {
            case "ALL": {
                return bookingRepository.findByBookerIdOrderByStartDesc(userId);
            }
            case "PAST": {
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(userId, LocalDateTime.now());
            }
            case "FUTURE": {
                return bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(userId, LocalDateTime.now());
            }
            case "CURRENT": {
                return bookingRepository.findByBookerIdAndStartBeforeAndEndIsAfterOrderByEndDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
            }
            case "WAITING": {
                return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.WAITING);
            }
            case "REJECTED": {
                return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED);
            }
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<Booking> getUserItemBooking(String state, long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        List<Item> itemList = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);
        List<Booking> bookingList = new ArrayList<>();
        switch (state) {
            case "ALL": {
                for (Item item : itemList) {
                    bookingList.addAll(bookingRepository.findByItemIdOrderByStartDesc(item.getId()));
                }
                return bookingList;
            }
            case "PAST": {
                for (Item item : itemList) {
                    bookingList.addAll(bookingRepository.findByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(),
                            LocalDateTime.now()));
                }
                return bookingList;
            }
            case "FUTURE": {
                for (Item item : itemList) {
                    bookingList.addAll(bookingRepository.findByItemIdAndStartIsAfterOrderByEndDesc(item.getId(),
                            LocalDateTime.now()));
                }
                return bookingList;
            }
            case "CURRENT": {
                for (Item item : itemList) {
                    bookingList.addAll(bookingRepository.findByItemIdAndStartBeforeAndEndIsAfterOrderByEndDesc(item.getId(),
                            LocalDateTime.now(), LocalDateTime.now()));
                }
                return bookingList;
            }
            case "WAITING": {
                for (Item item : itemList) {
                    bookingList.addAll(bookingRepository.findByItemIdAndStatusIsOrderByStartDesc(item.getId(),
                            BookingStatus.WAITING));
                }
                return bookingList;
            }
            case "REJECTED": {
                for (Item item : itemList) {
                    bookingList.addAll(bookingRepository.findByItemIdAndStatusIsOrderByStartDesc(item.getId(),
                            BookingStatus.REJECTED));
                }
                return bookingList;
            }

        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}
