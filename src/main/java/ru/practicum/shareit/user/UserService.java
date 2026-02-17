package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto updateUser(long id, UserDto userDto);
    UserDto getUserById(long id);
    List<UserDto> getAllUsers();
    void deleteUserById(long id);
}
