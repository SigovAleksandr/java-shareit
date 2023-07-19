package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    UserDto getUserById(long id);
    List<UserDto> getAllUsers();
    UserDto createUser(User user);
    UserDto updateUser(long userId, User user);
    void deleteUserById(long id);
}
