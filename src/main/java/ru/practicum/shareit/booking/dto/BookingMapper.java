package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static Booking toBookingFromAddDto(BookingAddDto bookingAddDto) {
        return new Booking(
                0,
                bookingAddDto.getStart(),
                bookingAddDto.getEnd(),
                null,
                null,
                null
        );
    }
}