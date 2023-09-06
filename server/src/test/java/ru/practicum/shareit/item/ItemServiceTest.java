package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.comment.dto.CommentAddDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void addItemSuccessTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        Item item = getItem(1);
        ItemDto itemDto = itemService.addItem(ItemMapper.toItemDto(item), user.getId());
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void getItemByIdSuccessTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), user.getId());
        ItemDto itemDto = itemService.getItemById(1, 1);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void getAllItemsSuccessTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        Item itemOne = getItem(1);
        Item itemTwo = getItem(2);
        itemService.addItem(ItemMapper.toItemDto(itemOne), user.getId());
        itemService.addItem(ItemMapper.toItemDto(itemTwo), user.getId());
        List<ItemDto> items = itemService.getItems(1);
        assertEquals(2, items.size());
    }

    @Test
    void updateItemSuccessTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), user.getId());
        item.setName("new name");
        item.setDescription("new description");
        item.setAvailable(false);
        ItemDto itemDto = itemService.updateItem(item.getId(), user.getId(), ItemMapper.toItemDto(item));
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void searchItemWithTestSuccessTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), user.getId());
        List<ItemDto> items = itemService.searchItems(user.getId(), "Description1");
        assertEquals(1, items.size());
    }

    @Test
    void addCommentSuccessTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        bookingService.approveBooking(1, 1, true);
        Thread.sleep(2000);
        CommentAddDto comment = getAddComment(1);
        CommentDto commentDto = itemService.addComment(comment, 2, 1);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
    }

    @Test
    void findItemCommentsSuccessTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        bookingService.approveBooking(1, 1, true);
        Thread.sleep(2000);
        CommentAddDto comment = getAddComment(1);
        itemService.addComment(comment, 2, 1);
        List<CommentDto> comments = itemService.findItemComments(item.getId());
        assertEquals(1, comments.size());
    }

    @Test
    void getItemNotFoundShouldReturnErrorTest() {
        try {
            itemService.getItemById(1, 1);
        } catch (ResourceNotFoundException e) {
            assertEquals("Item not found", e.getMessage());
        }
    }

    @Test
    void addItemWithNullAvailableShouldReturnErrorTest() {
        Item item = getItem(1);
        item.setAvailable(null);
        try {
            itemService.addItem(ItemMapper.toItemDto(item), 1);
        } catch (ValidationException e) {
            assertEquals("Available must not be null", e.getMessage());
        }
    }

    @Test
    void addItemWithNullNameShouldReturnErrorTest() {
        Item item = getItem(1);
        item.setName(null);
        try {
            itemService.addItem(ItemMapper.toItemDto(item), 1);
        } catch (ValidationException e) {
            assertEquals("Name must not be null", e.getMessage());
        }
    }

    @Test
    void addItemWithEmptyNameShouldReturnErrorTest() {
        Item item = getItem(1);
        item.setName("");
        try {
            itemService.addItem(ItemMapper.toItemDto(item), 1);
        } catch (ValidationException e) {
            assertEquals("Name must not be null", e.getMessage());
        }
    }

    @Test
    void addItemWithNullDescriptionShouldReturnErrorTest() {
        Item item = getItem(1);
        item.setDescription(null);
        try {
            itemService.addItem(ItemMapper.toItemDto(item), 1);
        } catch (ValidationException e) {
            assertEquals("Description must not be null", e.getMessage());
        }
    }

    @Test
    void addItemWithEmptyDescriptionShouldReturnErrorTest() {
        Item item = getItem(1);
        item.setDescription("");
        try {
            itemService.addItem(ItemMapper.toItemDto(item), 1);
        } catch (ValidationException e) {
            assertEquals("Description must not be null", e.getMessage());
        }
    }

    @Test
    void updateItemNotFoundShouldReturnErrorTest() {
        Item item = getItem(1);
        try {
            itemService.updateItem(1, 1, ItemMapper.toItemDto(item));
        } catch (ResourceNotFoundException e) {
            assertEquals("Item not found", e.getMessage());
        }
    }

    @Test
    void updateItemWithInvalidOwnerShouldReturnError() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), user.getId());
        try {
            itemService.updateItem(1, 2, ItemMapper.toItemDto(item));
        } catch (ResourceNotFoundException e) {
            assertEquals("Invalid owner id", e.getMessage());
        }
    }

    @Test
    void updateItemWithNullNameShouldUpdateWithOldNameTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), user.getId());
        item.setName(null);
        ItemDto itemDto = itemService.updateItem(1, 1, ItemMapper.toItemDto(item));
        assertNotEquals(item.getName(), itemDto.getName());
    }

    @Test
    void updateItemWithNullDescriptionShouldUpdateWithOldNameTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), user.getId());
        item.setDescription(null);
        ItemDto itemDto = itemService.updateItem(1, 1, ItemMapper.toItemDto(item));
        assertNotEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    void updateItemWithNullAvailableShouldUpdateWithOldNameTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), user.getId());
        item.setAvailable(null);
        ItemDto itemDto = itemService.updateItem(1, 1, ItemMapper.toItemDto(item));
        assertNotEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void addCommentWithEmptyTextShouldReturnErrorTest() {
        CommentAddDto commentAddDto = getAddComment(1);
        commentAddDto.setText("");
        try {
            itemService.addComment(commentAddDto, 1, 1);
        } catch (ValidationException e) {
            assertEquals("Comment must not be empty", e.getMessage());
        }
    }

    @Test
    void addCommentUserIsNotBookerShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        CommentAddDto comment = getAddComment(1);
        try {
            itemService.addComment(comment, 2, 1);
        } catch (ValidationException e) {
            assertEquals("This user is not the booker for item", e.getMessage());
        }
    }

    @Test
    void addCommentStartTimeInFutureShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().plusSeconds(3));
        bookingAddDto.setEnd(LocalDateTime.now().plusMinutes(10));
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        bookingService.approveBooking(1, 1, true);
        CommentAddDto comment = getAddComment(1);
        try {
            itemService.addComment(comment, 2, 1);
        } catch (ValidationException e) {
            assertEquals("Start time of booking in the future", e.getMessage());
        }
    }

    @Test
    void searchItemWithEmptyTextShouldReturnEmptyListTest() {
        List<ItemDto> items = itemService.searchItems(1, "");
        assertEquals(0, items.size());
    }

    @Test
    void addItemUserNotFoundShouldReturnErrorTest() {
        Item item = getItem(1);
        try {
            itemService.addItem(ItemMapper.toItemDto(item), 1);
        } catch (ResourceNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void getItemByIdForNonOwnerTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        ItemDto itemDto = itemService.getItemById(1, 2);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void getItemWithBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        bookingService.approveBooking(1, 1, true);
        BookingAddDto bookingAddDtoTwo = getAddBookingDto(item.getId());
        bookingAddDtoTwo.setStart(LocalDateTime.now().plusMinutes(1));
        bookingAddDtoTwo.setEnd(LocalDateTime.now().plusMinutes(2));
        Thread.sleep(2000);
        bookingService.addBooking(bookingAddDtoTwo, userTwo.getId());
        bookingService.approveBooking(2, 1, true);
        ItemDto itemDto = itemService.getItemById(1, 1);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNotNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getNextBooking());
    }

    @Test
    void getItemsWithBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        bookingService.approveBooking(1, 1, true);
        BookingAddDto bookingAddDtoTwo = getAddBookingDto(item.getId());
        bookingAddDtoTwo.setStart(LocalDateTime.now().plusMinutes(1));
        bookingAddDtoTwo.setEnd(LocalDateTime.now().plusMinutes(2));
        Thread.sleep(2000);
        bookingService.addBooking(bookingAddDtoTwo, userTwo.getId());
        bookingService.approveBooking(2, 1, true);
        List<ItemDto> items = itemService.getItems(1);
        ItemDto itemDto = items.get(0);
        assertEquals(1, items.size());
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNotNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getNextBooking());
    }

    @Test
    void getItemOnlyLastBookingSetTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        bookingService.approveBooking(1, 1, true);
        Thread.sleep(2000);
        BookingAddDto bookingAddDtoTwo = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDtoTwo, userTwo.getId());
        bookingService.approveBooking(2, 1, true);
        Thread.sleep(2000);
        ItemDto itemDto = itemService.getItemById(1, 1);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNotNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    void getItemWithTwoLastBookingTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setEnd(LocalDateTime.now().plusMinutes(2));
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        bookingService.approveBooking(1, 1, true);
    }

    private User getUser(long id) {
        return User.builder().id(id).name("User " + id).email("user" + id + "@user.com").build();
    }

    private Item getItem(long id) {
        return Item.builder().id(id).name("Item " + id).description("Description" + id).available(true).build();
    }

    private CommentAddDto getAddComment(long id) {
        return CommentAddDto.builder().id(id).text("Text " + id).build();
    }

    private BookingAddDto getAddBookingDto(long itemId) {
        return BookingAddDto.builder().itemId(itemId).start(LocalDateTime.now().plusSeconds(1)).end(LocalDateTime.now().plusSeconds(2)).build();
    }
}
