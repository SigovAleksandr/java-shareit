package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking getBookingById(long bookingId, long userId);

    Booking addBooking(BookingAddDto booking, long userId);

    Booking approveBooking(long bookingId, long userId, boolean approve);

    List<Booking> getUserBooking(String state, long userId);

    List<Booking> getUserItemBooking(String state, long userId);
}
