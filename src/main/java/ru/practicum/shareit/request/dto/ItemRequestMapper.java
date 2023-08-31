package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .items(null)
                .build();
    }

    public ItemRequest fromItemRequestDto(ItemRequestAddDto itemRequestDto) {
        return ItemRequest.builder()
                .id(0)
                .description(itemRequestDto.getDescription())
                .requestor(null)
                .created(LocalDateTime.now())
                .build();
    }
}
