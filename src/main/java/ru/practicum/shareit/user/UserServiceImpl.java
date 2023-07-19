package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
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

    public UserDto updateUser(long userId, User user) {
        return userStorage.updateUser(userId, user);
    }

    public UserDto createUser(User user) {
        return userStorage.createUser(user);
    }

    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }
}
