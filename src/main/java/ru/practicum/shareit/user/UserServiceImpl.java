package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public UserDto getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        return userStorage.updateUser(userId, userDto);
    }

    public UserDto createUser(@Valid UserDto userDto) {
        return userStorage.createUser(userDto);
    }

    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }
}
