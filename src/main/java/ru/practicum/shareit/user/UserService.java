package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface UserService {

    UserDto getUserById(long id);
    List<UserDto> getAllUsers();
    UserDto createUser(User user);
    UserDto updateUser(long userId, User user);
    void deleteUserById(long id);
}
