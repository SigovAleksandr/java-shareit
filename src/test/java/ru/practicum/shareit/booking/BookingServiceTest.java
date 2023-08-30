package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
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
public class BookingServiceTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void getBookingByIdSuccessTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        BookingDto bookingDto = bookingService.getBookingById(1, 1);
        assertEquals(bookingAddDto.getItemId(), bookingDto.getItem().getId());
        assertEquals(userTwo.getId(), bookingDto.getBooker().getId());
    }

    @Test
    void createBookingSuccessTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        BookingDto bookingDto = bookingService.addBooking(bookingAddDto, userTwo.getId());
        assertEquals(bookingAddDto.getItemId(), bookingDto.getItem().getId());
        assertEquals(userTwo.getId(), bookingDto.getBooker().getId());
    }

    @Test
    void approveBookingSuccessTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        BookingDto bookingDto = bookingService.approveBooking(1, 1, true);
        assertEquals(bookingAddDto.getItemId(), bookingDto.getItem().getId());
        assertEquals(userTwo.getId(), bookingDto.getBooker().getId());
    }

    @Test
    void getAllUserBookingTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        List<BookingDto> bookings = bookingService.getUserBooking("ALL", 2, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getPastUserBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        Thread.sleep(2000);
        List<BookingDto> bookings = bookingService.getUserBooking("PAST", 2, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getFutureUserBookingTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusMinutes(2));
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        List<BookingDto> bookings = bookingService.getUserBooking("FUTURE", 2, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getCurrentUserBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().plusSeconds(2));
        bookingAddDto.setEnd(LocalDateTime.now().plusMinutes(2));
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        Thread.sleep(3000);
        List<BookingDto> bookings = bookingService.getUserBooking("CURRENT", 2, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getWaitingUserBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().plusSeconds(2));
        bookingAddDto.setEnd(LocalDateTime.now().plusMinutes(2));
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        Thread.sleep(3000);
        List<BookingDto> bookings = bookingService.getUserBooking("WAITING", 2, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getRejectedUserBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        Thread.sleep(2000);
        bookingService.approveBooking(1, 1, false);
        List<BookingDto> bookings = bookingService.getUserBooking("REJECTED", 2, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getIncorrectStateUserBookingTest() {
        User userOne = getUser(1);
        userService.createUser(UserMapper.toUserDto(userOne));
        try {
            bookingService.getUserBooking("INVALID", 1, 0, 20);
        } catch (ValidationException e) {
            assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
        }
    }

    @Test
    void getUserBookingUserNotFoundTest() {
        try {
            bookingService.getUserBooking("INVALID", 1, 0, 20);
        } catch (ResourceNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void getAllUserItemBookingTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        List<BookingDto> bookings = bookingService.getUserItemBooking("ALL", 1, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getPastUserItemBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        Thread.sleep(2000);
        List<BookingDto> bookings = bookingService.getUserItemBooking("PAST", 1, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getFutureUserItemBookingTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingAddDto.setEnd(LocalDateTime.now().plusMinutes(2));
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        List<BookingDto> bookings = bookingService.getUserItemBooking("FUTURE", 1, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getCurrentUserItemBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().plusSeconds(2));
        bookingAddDto.setEnd(LocalDateTime.now().plusMinutes(2));
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        Thread.sleep(3000);
        List<BookingDto> bookings = bookingService.getUserItemBooking("CURRENT", 1, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getWaitingUserItemBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().plusSeconds(2));
        bookingAddDto.setEnd(LocalDateTime.now().plusMinutes(2));
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        Thread.sleep(3000);
        List<BookingDto> bookings = bookingService.getUserItemBooking("WAITING", 1, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getRejectedUserItemBookingTest() throws InterruptedException {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        Thread.sleep(2000);
        bookingService.approveBooking(1, 1, false);
        List<BookingDto> bookings = bookingService.getUserItemBooking("REJECTED", 1, 0, 20);
        assertEquals(1, bookings.size());
    }

    @Test
    void getIncorrectStateUserItemBookingTest() {
        User userOne = getUser(1);
        userService.createUser(UserMapper.toUserDto(userOne));
        try {
            bookingService.getUserItemBooking("INVALID", 1, 0, 20);
        } catch (ValidationException e) {
            assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
        }
    }

    @Test
    void getUserItemBookingUserNotFoundTest() {
        try {
            bookingService.getUserItemBooking("INVALID", 1, 0, 20);
        } catch (ResourceNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void getBookingByIdBookingNotFoundShouldReturnErrorTest() {
        try {
            bookingService.getBookingById(1, 1);
        } catch (ResourceNotFoundException e) {
            assertEquals("Booking not found", e.getMessage());
        }
    }

    @Test
    void getBookingByIdUserNotFoundShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        try {
            bookingService.getBookingById(1, 3);
        } catch (ResourceNotFoundException e) {
            assertEquals("Invalid user", e.getMessage());
        }
    }

    @Test
    void addBookingUserNotFoundShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        try {
            bookingService.addBooking(bookingAddDto, 3);
        } catch (ResourceNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void addBookingItemNotFoundShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(2);
        try {
            bookingService.addBooking(bookingAddDto, 2);
        } catch (ResourceNotFoundException e) {
            assertEquals("Item not found", e.getMessage());
        }
    }

    @Test
    void addBookingInvalidDateShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().minusSeconds(10));
        try {
            bookingService.addBooking(bookingAddDto, 2);
        } catch (ValidationException e) {
            assertEquals("Incorrect start or end date", e.getMessage());
        }
    }

    @Test
    void addBookingFromOwnerShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        try {
            bookingService.addBooking(bookingAddDto, 1);
        } catch (ResourceNotFoundException e) {
            assertEquals("Booking items from owner is unavailable", e.getMessage());
        }
    }

    @Test
    void approveBookingNotFoundShouldReturnErrorTest() {
        try {
            bookingService.approveBooking(1, 1, true);
        } catch (ResourceNotFoundException e) {
            assertEquals("Booking not found", e.getMessage());
        }
    }

    @Test
    void approveBookingAlreadyApprovedShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, 2);
        bookingService.approveBooking(1, 1, true);
        try {
            bookingService.approveBooking(1, 1, true);
        } catch (ValidationException e) {
            assertEquals("Booking already approved", e.getMessage());
        }
    }

    @Test
    void approveBookingUserIdNotAnOwnerShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, 2);
        try {
            bookingService.approveBooking(1, 2, true);
        } catch (ResourceNotFoundException e) {
            assertEquals("User with this id is not an owner", e.getMessage());
        }
    }

    @Test
    void getBookingByIdUserIdIsBookerShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingService.addBooking(bookingAddDto, userTwo.getId());
        try {
            bookingService.getBookingById(1, 2);
        } catch (ResourceNotFoundException e) {
            assertEquals("Invalid user", e.getMessage());
        }
    }

    @Test
    void addBookingItemIsUnavailableShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        item.setAvailable(false);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        try {
            bookingService.addBooking(bookingAddDto, 2);
        } catch (ValidationException e) {
            assertEquals("This item is unavailable", e.getMessage());
        }
    }

    @Test
    void addBookingEndDateIsBeforeNowShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setEnd(LocalDateTime.now().minusSeconds(10));
        try {
            bookingService.addBooking(bookingAddDto, 2);
        } catch (ValidationException e) {
            assertEquals("Incorrect start or end date", e.getMessage());
        }
    }

    @Test
    void addBookingEndDateEqualsStartShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setEnd(bookingAddDto.getStart());
        try {
            bookingService.addBooking(bookingAddDto, 2);
        } catch (ValidationException e) {
            assertEquals("Incorrect start or end date", e.getMessage());
        }
    }

    @Test
    void addBookingEndDateBeforeStartShouldReturnErrorTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        Item item = getItem(1);
        itemService.addItem(ItemMapper.toItemDto(item), userOne.getId());
        BookingAddDto bookingAddDto = getAddBookingDto(item.getId());
        bookingAddDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingAddDto.setEnd(LocalDateTime.now().minusSeconds(30));
        try {
            bookingService.addBooking(bookingAddDto, 2);
        } catch (ValidationException e) {
            assertEquals("Incorrect start or end date", e.getMessage());
        }
    }

    private User getUser(long id) {
        return User.builder().id(id).name("User " + id).email("user" + id + "@user.com").build();
    }

    private Item getItem(long id) {
        return Item.builder().id(id).name("Item " + id).description("Description" + id).available(true).build();
    }

    private BookingAddDto getAddBookingDto(long itemId) {
        return BookingAddDto.builder().itemId(itemId).start(LocalDateTime.now().plusSeconds(1)).end(LocalDateTime.now().plusSeconds(2)).build();
    }
}
