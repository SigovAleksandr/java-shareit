package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.utils.BaseConstants.HEADER;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

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
    public List<BookingDto> getUserBooking(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader(HEADER) long userId) {
        return bookingService.getUserBooking(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemBooking(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(HEADER) long userId) {
        return bookingService.getUserItemBooking(state, userId);
    }
}
