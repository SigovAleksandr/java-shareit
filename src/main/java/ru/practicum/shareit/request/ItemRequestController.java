package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.utils.BaseConstants.HEADER;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestAddDto itemRequestAddDto,
                                        @RequestHeader(HEADER) long userId) {
        return itemRequestService.createRequest(itemRequestAddDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable long requestId,
                                         @RequestHeader(HEADER) long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsForUser(@RequestHeader(HEADER) long userId) {
        return itemRequestService.getRequestsForUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsWithParam(@RequestParam(required = false) Long from,
                                                        @RequestParam(required = false) Long size,
                                                        @RequestHeader(HEADER) long userId) {
        if (from == null || size == null) {
            return itemRequestService.getAllRequests(userId);
        }
        return itemRequestService.getAllRequestsParametrized(userId, from, size);
    }
}
