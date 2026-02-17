package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping({"/{id}"})
    public UserDto updateUser(@PathVariable long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @GetMapping({"/{id}"})
    public UserDto getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping({"/{id}"})
    public void deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);
    }
}