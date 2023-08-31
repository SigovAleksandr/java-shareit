package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestServiceImpl(UserRepository userRepository,
                                  ItemRepository itemRepository,
                                  ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequestDto createRequest(ItemRequestAddDto itemRequestAddDto, long userId) {
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestAddDto);
        itemRequest.setRequestor(userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found")));
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ResourceNotFoundException("Request not found"));
        List<Item> items = itemRepository.findByRequestId(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(request);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getRequestsForUser(long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        List<ItemRequest> requestList = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        List<Long> requestIds = requestList.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);
        List<ItemRequestDto> requestDtoList = requestList.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        for (ItemRequestDto requestDto : requestDtoList) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequestId() == requestDto.getId())
                    .collect(Collectors.toList());
            requestDto.setItems(requestItems);
        }
        return requestDtoList;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        List<ItemRequest> requestList = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        if (!isRequester(requestList, userId)) {
            return Collections.emptyList();
        }
        List<Long> requestIds = requestList.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsMap = itemRepository.findByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        List<ItemRequestDto> requestDtoList = requestList.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        for (ItemRequestDto requestDto : requestDtoList) {
            List<Item> requestItems = itemsMap.getOrDefault(requestDto.getId(), new ArrayList<>());
            requestDto.setItems(requestItems);
        }
        return requestDtoList;
    }

    @Override
    public List<ItemRequestDto> getAllRequestsParametrized(long userId, long from, long size) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        PageRequest pageable = PageRequest.of((int) Math.floor(from / size), (int) size, Sort.by("created").descending());
        Page<ItemRequest> requestPage = itemRequestRepository.findByRequesterOrOwner(userId, pageable);
        if (isRequester(requestPage.getContent(), userId)) {
            return Collections.emptyList();
        }
        List<ItemRequestDto> requestDtoList = requestPage.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        List<Long> requestIds = requestDtoList.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsMap = itemRepository.findByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        for (ItemRequestDto requestDto : requestDtoList) {
            List<Item> requestItems = itemsMap.getOrDefault(requestDto.getId(), new ArrayList<>());
            requestDto.setItems(requestItems);
        }
        return requestDtoList;
    }

    private boolean isRequester(List<ItemRequest> requestList, long userId) {
        for (ItemRequest request : requestList) {
            if (request.getRequestor().getId() == userId) {
                return true;
            }
        }
        return false;
    }
}
