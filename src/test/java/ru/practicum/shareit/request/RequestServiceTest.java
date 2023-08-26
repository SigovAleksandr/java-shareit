package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceTest {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Test
    void createRequestSuccessTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        Item item = getItem(1);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        itemService.addItem(ItemMapper.toItemDto(item), userTwo.getId());
        ItemRequestAddDto itemRequestAddDto = getItemRequestAddDto();
        ItemRequestDto itemRequestDto =
                itemRequestService.createRequest(itemRequestAddDto, userOne.getId());
        assertEquals(itemRequestAddDto.getRequester(), itemRequestDto.getRequester());
    }

    @Test
    void getRequestByIdSuccessTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        Item item = getItem(1);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        itemService.addItem(ItemMapper.toItemDto(item), userTwo.getId());
        ItemRequestAddDto itemRequestAddDto = getItemRequestAddDto();
        itemRequestService.createRequest(itemRequestAddDto, userOne.getId());
        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(1, 1);
        assertEquals(itemRequestAddDto.getRequester(), itemRequestDto.getRequester());
    }

    @Test
    void getRequestForUserSuccessTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        Item item = getItem(1);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        itemService.addItem(ItemMapper.toItemDto(item), userTwo.getId());
        ItemRequestAddDto itemRequestAddDto = getItemRequestAddDto();
        itemRequestService.createRequest(itemRequestAddDto, userOne.getId());
        List<ItemRequestDto> itemRequestDto = itemRequestService.getRequestsForUser(1);
        assertEquals(1, itemRequestDto.size());
    }

    @Test
    void getAllRequestsSuccessTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        Item item = getItem(1);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        itemService.addItem(ItemMapper.toItemDto(item), userTwo.getId());
        ItemRequestAddDto itemRequestAddDto = getItemRequestAddDto();
        itemRequestService.createRequest(itemRequestAddDto, userOne.getId());
        List<ItemRequestDto> itemRequestDto = itemRequestService.getAllRequests(2);
        assertEquals(1, itemRequestDto.size());
    }

    @Test
    void getAllRequestsParametrizedSuccessTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        Item item = getItem(1);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        itemService.addItem(ItemMapper.toItemDto(item), userTwo.getId());
        ItemRequestAddDto itemRequestAddDto = getItemRequestAddDto();
        itemRequestService.createRequest(itemRequestAddDto, userOne.getId());
        List<ItemRequestDto> itemRequestDto = itemRequestService.getAllRequestsParametrized(2, 0, 1);
        assertEquals(1, itemRequestDto.size());
    }

    private ItemRequestAddDto getItemRequestAddDto() {
        return ItemRequestAddDto.builder()
                .description("test")
                .requester(getUser(1))
                .created(LocalDateTime.now().plusSeconds(1))
                .build();
    }

    private User getUser(long id) {
        return User.builder().id(id).name("User " + id).email("user" + id + "@user.com").build();
    }

    private Item getItem(long id) {
        return Item.builder().id(id).name("Item " + id).description("Description" + id).available(true).build();
    }
}
