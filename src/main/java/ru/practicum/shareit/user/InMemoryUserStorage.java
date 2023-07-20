package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.InternalErrorException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
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
    public UserDto createUser(@Valid UserDto userDto) {
        for (User usr : users.values()) {
            if (usr.getEmail().equals(userDto.getEmail())) {
                throw new InternalErrorException("User with this email already exist");
            }
        }
        userDto.setId(getId());
        users.put(userDto.getId(), UserMapper.fromUserDto(userDto));
        return userDto;
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        if (!users.containsKey(userId)) {
            throw new ValidationException("User with this id not exist");
        }
        for (User usr : users.values()) {
            if (usr.getEmail().equals(userDto.getEmail())) {
                if (usr.getId() != userId) {
                    throw new InternalErrorException("User with this email already exist");
                }
            }
        }
        User newUser = users.get(userId);
        if (userDto.getName() != null)
            newUser.setName(userDto.getName());
        if (userDto.getEmail() != null)
            newUser.setEmail(userDto.getEmail());
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
