package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(long userId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfterOrderByEndDesc(long userId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndIsAfterOrderByEndDesc(long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusIsOrderByStartDesc(long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemIdOrderByStartDesc(long itemId);

    Page<Booking> findByItemIdInAndEndIsBeforeOrderByEndDesc(List<Long> ids, LocalDateTime time, Pageable pageable);

    Page<Booking> findByItemIdInAndStartIsAfterOrderByEndDesc(List<Long> itemIds, LocalDateTime time, Pageable pageable);

    Page<Booking> findByItemIdInAndStartBeforeAndEndIsAfterOrderByEndDesc(List<Long> itemIds, LocalDateTime start,
                                                                          LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemIdInAndStatusIsOrderByStartDesc(List<Long> itemIds, BookingStatus status, Pageable pageable);

    @Query("select b " + "from Booking b " + "where b.item.id = ?1 " + "and b.status <> 'REJECTED' " + "and b.end > CURRENT_TIMESTAMP " + "order by b.start asc")
    List<Booking> findNextBooking(long itemId);

    @Query("select b " + "from Booking b " + "where b.item.id = ?1 " + "and b.status <> 'REJECTED' " + "and b.start < current_timestamp " + "order by b.end asc")
    List<Booking> findLastBooking(long itemId);

    List<Booking> findByBookerIdAndItemId(long userId, long itemid);

    Page<Booking> findByItemIdInOrderByStartDesc(List<Long> ids, Pageable pageable);
}
