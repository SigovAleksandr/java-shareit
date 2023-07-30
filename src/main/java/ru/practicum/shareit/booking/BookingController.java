package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    public static final String HEADER = "X-Sharer-User-Id";

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader(HEADER) long userId) {
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingAddDto bookingAddDto,
                                 @RequestHeader(HEADER) long userId) {
        return BookingMapper.toBookingDto(bookingService.addBooking(bookingAddDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateItem(@PathVariable long bookingId,
                                 @RequestHeader(HEADER) long userId,
                                 @RequestParam("approved") boolean approved) {
        return BookingMapper.toBookingDto(bookingService.approveBooking(bookingId, userId, approved));
    }

    @GetMapping()
    public List<BookingDto> getUserBooking(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader(HEADER) long userId) {
        return bookingService.getUserBooking(state, userId).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemBooking(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(HEADER) long userId) {
        return bookingService.getUserItemBooking(state, userId).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
