package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.InternalErrorException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {

    private final UserService userService;

    @Test
    void createUserSuccessTest() {
        User user = getUser(1);
        UserDto savedUser = userService.createUser(UserMapper.toUserDto(user));
        assertEquals(user.getId(), savedUser.getId());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getName(), savedUser.getName());
    }

    @Test
    void getAllUsersSuccessTest() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        List<UserDto> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void getUserByIdSuccessTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        UserDto userDto = userService.getUserById(1);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void updateUserSuccessTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        user.setName("update");
        user.setEmail("update@email.com");
        UserDto userDto = userService.updateUser(1, UserMapper.toUserDto(user));
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void deleteUserSuccessTest() {
        User user = getUser(1);
        userService.createUser(UserMapper.toUserDto(user));
        userService.deleteUserById(1);
        try {
            userService.getUserById(1);
        } catch (ResourceNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void updateUserWithExistingEmailShouldReturnError() {
        User userOne = getUser(1);
        User userTwo = getUser(2);
        userService.createUser(UserMapper.toUserDto(userOne));
        userService.createUser(UserMapper.toUserDto(userTwo));
        userOne.setEmail(userTwo.getEmail());
        try {
            userService.updateUser(1, UserMapper.toUserDto(userOne));
        } catch (InternalErrorException e) {
            assertEquals("User with this email already exist", e.getMessage());
            assertNotEquals(userOne.getEmail(), userService.getUserById(1).getEmail());
        }
    }

    @Test
    void updateUserWithNullNameShouldUpdateWithOldName() {
        User userOne = getUser(1);
        userService.createUser(UserMapper.toUserDto(userOne));
        userOne.setName(null);
        UserDto userDto = userService.updateUser(1, UserMapper.toUserDto(userOne));
        assertEquals("User 1", userDto.getName());
    }

    @Test
    void updateUserWithNullEmailShouldUpdateWithOldEmail() {
        User userOne = getUser(1);
        userService.createUser(UserMapper.toUserDto(userOne));
        userOne.setEmail(null);
        UserDto userDto = userService.updateUser(1, UserMapper.toUserDto(userOne));
        assertEquals("Email1@user.com", userDto.getEmail());
    }

    private User getUser(long id) {
        return User.builder()
                .id(id)
                .name("User " + id)
                .email("Email" + id + "@user.com")
                .build();
    }
}