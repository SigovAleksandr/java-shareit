package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found"));
        if (userId == booking.getBooker().getId() ||
                userId == itemRepository.findById(booking.getItem().getId()).orElseThrow().getOwner().getId()) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ResourceNotFoundException("Invalid user");
        }
    }

    @Override
    public BookingDto addBooking(BookingAddDto booking, long userId) {
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
        } else if (booker.getId() == item.getOwner().getId()) {
            throw new ResourceNotFoundException("Booking items from owner is unavailable");
        }
        bookingAdd.setBooker(booker);
        bookingAdd.setStatus(BookingStatus.WAITING);
        bookingAdd.setItem(item);
        return BookingMapper.toBookingDto(bookingRepository.save(bookingAdd));
    }

    @Override
    public BookingDto approveBooking(long bookingId, long userId, boolean approve) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found"));
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Booking already approved");
        }
        if (itemRepository.findById(booking.getItem().getId()).orElseThrow().getOwner().getId() != userId) {
            throw new ResourceNotFoundException("User with this id is not an owner");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getUserBooking(String state, long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        switch (state) {
            case "ALL": {
                return bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "PAST": {
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "FUTURE": {
                List<Booking> bookingList = bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(userId, LocalDateTime.now().minusSeconds(3));
                return bookingList.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT": {
                return bookingRepository.findByBookerIdAndStartBeforeAndEndIsAfterOrderByEndDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "WAITING": {
                return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.WAITING).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "REJECTED": {
                return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }




    @Override
    public List<BookingDto> getUserItemBooking(String state, long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        List<Item> itemList = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);
        List<Booking> bookingList;
        switch (state) {
            case "ALL": {
                bookingList = bookingRepository.findByItemIdIn(itemList.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()));
                return bookingList.stream()
                        .map(BookingMapper::toBookingDto)
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case "PAST": {
                bookingList = bookingRepository.findByItemIdInAndEndIsBeforeOrderByEndDesc(itemList.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()), LocalDateTime.now());
                return bookingList.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "FUTURE": {
                bookingList = bookingRepository.findByItemIdInAndStartIsAfterOrderByEndDesc(itemList.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()), LocalDateTime.now().minusSeconds(3));
                return bookingList.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT": {
                bookingList = bookingRepository.findByItemIdInAndStartBeforeAndEndIsAfterOrderByEndDesc(itemList.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()), LocalDateTime.now(), LocalDateTime.now());
                return bookingList.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "WAITING": {
                bookingList = bookingRepository.findByItemIdInAndStatusIsOrderByStartDesc(itemList.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()), BookingStatus.WAITING);
                return bookingList.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "REJECTED": {
                bookingList = bookingRepository.findByItemIdInAndStatusIsOrderByStartDesc(itemList.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()), BookingStatus.REJECTED);
                return bookingList.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}

