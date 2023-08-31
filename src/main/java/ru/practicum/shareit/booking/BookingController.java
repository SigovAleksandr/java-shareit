package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.utils.BaseConstants.HEADER;

@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader(HEADER) long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingAddDto bookingAddDto,
                                 @RequestHeader(HEADER) long userId) {
        return bookingService.addBooking(bookingAddDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateItem(@PathVariable long bookingId,
                                 @RequestHeader(HEADER) long userId,
                                 @RequestParam("approved") boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping()
    public List<BookingDto> getUserBooking(@RequestParam(required = false, defaultValue = "ALL") @Valid BookingTimeState state,
                               @RequestHeader(HEADER) long userId,
                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                               @Positive @RequestParam(defaultValue = "20") int size) {
        return bookingService.getUserBooking(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemBooking(@RequestParam(defaultValue = "ALL") @Valid BookingTimeState state,
                                               @RequestHeader(HEADER) long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "20") int size) {
        return bookingService.getUserItemBooking(state, userId, from, size);
    }
}
