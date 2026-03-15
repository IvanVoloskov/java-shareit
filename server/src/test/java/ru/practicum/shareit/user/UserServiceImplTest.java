package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@mail.com");
    }

    @Test
    void createUser_ShouldSaveUser_WhenDataIsValid() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@mail.com", result.getEmail());

        verify(userRepository).findByEmail(userDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowConflict_WhenEmailExists() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john@mail.com", result.getEmail());
    }

    @Test
    void getUserById_ShouldThrowNotFound_WhenUserNotExists() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john@mail.com", result.get(0).getEmail());
    }

    @Test
    void updateUser_ShouldUpdateName_WhenNameProvided() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("Updated Name", result.getName());
        assertEquals("john@mail.com", result.getEmail()); // email не изменился
    }

    @Test
    void updateUser_ShouldUpdateEmail_WhenEmailProvided() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("new@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("John", result.getName());
        assertEquals("new@mail.com", result.getEmail());
    }

    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowNotFound_WhenUserNotExists() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUserById(99L));

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateUser_ShouldThrow_WhenEmailAlreadyUsedByAnotherUser() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("existing@mail.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("existing@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("existing@mail.com")).thenReturn(Optional.of(anotherUser));

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_ShouldUpdateEmail_WhenEmailIsSameAsCurrent() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("john@mail.com"); // тот же email

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("john@mail.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldNotUpdate_WhenAllFieldsNull() {
        UserDto updateDto = new UserDto(); // все поля null

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("John", result.getName());
        assertEquals("john@mail.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldThrow_WhenEmailIsInvalid() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("invalid-email");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_ShouldThrow_WhenEmailIsBlank() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_ShouldThrow_WhenNameIsBlank() {
        UserDto updateDto = new UserDto();
        updateDto.setName("");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, updateDto));
    }
}