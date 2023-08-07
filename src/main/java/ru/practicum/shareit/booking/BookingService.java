package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto getBookingById(long bookingId, long userId);

    BookingDto addBooking(BookingAddDto booking, long userId);

    BookingDto approveBooking(long bookingId, long userId, boolean approve);

    List<BookingDto> getUserBooking(String state, long userId);

    List<BookingDto> getUserItemBooking(String state, long userId);
}
