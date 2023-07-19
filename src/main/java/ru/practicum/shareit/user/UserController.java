package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") long id) {
        return userService.getUserById(id);
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody User user, @PathVariable("id") long userId) {
        return userService.updateUser(userId, user);
    }

    @PostMapping()
    public UserDto createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") long id) {
        userService.deleteUserById(id);
    }
}
