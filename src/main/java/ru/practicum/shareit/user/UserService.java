package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Service
public interface UserService {

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    UserDto createUser(@Valid UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUserById(long id);
}
