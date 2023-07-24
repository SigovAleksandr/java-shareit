package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    UserDto createUser(@Valid UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUserById(long id);
}
