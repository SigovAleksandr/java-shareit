package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBookingFromAddDto(BookingAddDto bookingAddDto) {
        return Booking.builder()
                .id(0)
                .start(bookingAddDto.getStart())
                .end(bookingAddDto.getEnd())
                .item(null)
                .booker(null)
                .status(null)
                .build();
    }
}
