package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    Optional<User> getUserById(long id);

    List<User> getAllUsers();

    User createUser(UserDto userDto);

    User updateUser(long userId, UserDto userDto);

    void deleteUserById(long id);
}
