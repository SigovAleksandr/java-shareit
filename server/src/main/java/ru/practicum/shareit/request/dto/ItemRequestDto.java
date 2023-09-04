package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;
    private List<Item> items;
}
