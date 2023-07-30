package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartIsAfterOrderByEndDesc(long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartBeforeAndEndIsAfterOrderByEndDesc(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(long userId, BookingStatus status);

    List<Booking> findByItemIdOrderByStartDesc(long itemId);

    List<Booking> findByItemIdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime time);

    List<Booking> findByItemIdAndStartIsAfterOrderByEndDesc(long itemId, LocalDateTime time);

    List<Booking> findByItemIdAndStartBeforeAndEndIsAfterOrderByEndDesc(long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemIdAndStatusIsOrderByStartDesc(long itemId, BookingStatus status);
}
