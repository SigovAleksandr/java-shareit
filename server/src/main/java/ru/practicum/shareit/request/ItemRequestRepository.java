package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(long userId);

    @Query(
            value = "SELECT * from requests AS r " +
                    "join items as i on i.request_id = r.id " +
                    "where r.requester_id = ?1 " +
                    "or i.owner_id = ?1",
            nativeQuery = true
    )
    Page<ItemRequest> findByRequesterOrOwner(long userId, Pageable pageable);
}
