package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalErrorException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found")));
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        User newUser = UserMapper.fromUserDto(userDto);
        User user = UserMapper.fromUserDto(getUserById(userId));
        for (UserDto usr : getAllUsers()) {
            if (usr.getEmail().equals(newUser.getEmail())) {
                if (usr.getId() != userId) {
                    throw new InternalErrorException("User with this email already exist");
                }
            }
        }
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            user.setEmail(newUser.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }
}
