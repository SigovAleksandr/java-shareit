package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("select i\n" +
            "from Item i \n" +
            "where upper(i.name) like upper(concat('%', ?1, '%'))\n" +
            "or upper(i.description) like upper(concat('%', ?1, '%'))\n" +
            "and i.available = true")
    List<Item> search(String text);

    List<Item> findByRequestId(long requestId);

    @Query("SELECT i FROM Item i WHERE i.requestId IN :requestIds")
    List<Item> findByRequestIdIn(@Param("requestIds") List<Long> requestIds);
}
