package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestAddDto itemRequestAddDto, long userId);

    ItemRequestDto getRequestById(long requestId, long userId);

    List<ItemRequestDto> getRequestsForUser(long userId);

    List<ItemRequestDto> getAllRequests(long userId);

    List<ItemRequestDto> getAllRequestsParametrized(long userId, long from, long size);
}
