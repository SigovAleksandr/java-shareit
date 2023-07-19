package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.InternalErrorException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private long id = 0;

    private long getId() {
        id = id + 1;
        return id;
    }

    @Override
    public UserDto getUserById(long id) {
        if (!users.containsKey(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> list = new ArrayList<>();
        for (User u : users.values()) {
            list.add(UserMapper.toUserDto(u));
        }
        return list;
    }

    @Override
    public UserDto createUser(User user) {
        for (User usr : users.values()) {
            if (usr.getEmail().equals(user.getEmail())) {
                throw new InternalErrorException("User with this email already exist");
            }
        }
        user.setId(getId());
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new ValidationException("User with this id not exist");
        }
        for (User usr : users.values()) {
            if (usr.getEmail().equals(user.getEmail())) {
                if (usr.getId() != userId) {
                    throw new InternalErrorException("User with this email already exist");
                }
            }
        }
        User newUser = users.get(userId);
        if (user.getName() != null)
            newUser.setName(user.getName());
        if (user.getEmail() != null)
            newUser.setEmail(user.getEmail());
        newUser.setId(userId);
        users.put(userId, newUser);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public void deleteUserById(long id) {
        if (!users.containsKey(id)) {
            throw new ValidationException("User with this id not exist");
        }
        users.remove(id);
    }
}
