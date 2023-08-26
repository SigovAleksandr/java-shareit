package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(null)
                .build();
    }

    public static ItemRequest fromItemRequestDto(ItemRequestAddDto itemRequestDto) {
        return ItemRequest.builder()
                .id(0)
                .description(itemRequestDto.getDescription())
                .requestor(null)
                .created(LocalDateTime.now())
                .build();
    }
}
