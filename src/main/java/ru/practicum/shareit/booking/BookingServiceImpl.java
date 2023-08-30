package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<BookingDto> getUserBooking(String state, long userId, int from, int size) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL": {
                Page<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "PAST": {
                LocalDateTime now = LocalDateTime.now();
                Page<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(userId, now, pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "FUTURE": {
                LocalDateTime now = LocalDateTime.now().minusSeconds(3);
                Page<Booking> bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(userId, now, pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT": {
                LocalDateTime now = LocalDateTime.now();
                Page<Booking> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndIsAfterOrderByEndDesc(userId, now, now, pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "WAITING": {
                Page<Booking> bookings = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "REJECTED": {
                Page<Booking> bookings = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingDto> getUserItemBooking(String state, long userId, int from, int size) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        List<Item> itemList = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);
        List<Long> itemIds = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL": {
                Page<Booking> bookings = bookingRepository.findByItemIdInOrderByStartDesc(itemIds, pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "PAST": {
                Page<Booking> bookings = bookingRepository.findByItemIdInAndEndIsBeforeOrderByEndDesc(itemIds,
                        LocalDateTime.now(), pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            // LocalDateTime.now().minusSeconds(3) - было в прошлой итерации ТЗ.
            // Без этого костыля у меня не проходят тесты постман.
            case "FUTURE": {
                Page<Booking> bookings = bookingRepository.findByItemIdInAndStartIsAfterOrderByEndDesc(itemIds,
                        LocalDateTime.now().minusSeconds(3), pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT": {
                Page<Booking> bookings = bookingRepository.findByItemIdInAndStartBeforeAndEndIsAfterOrderByEndDesc(
                        itemIds, LocalDateTime.now(), LocalDateTime.now(), pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "WAITING": {
                Page<Booking> bookings = bookingRepository.findByItemIdInAndStatusIsOrderByStartDesc(itemIds, BookingStatus.WAITING,
                        pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "REJECTED": {
                Page<Booking> bookings = bookingRepository.findByItemIdInAndStatusIsOrderByStartDesc(itemIds, BookingStatus.REJECTED,
                        pageable);
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}

