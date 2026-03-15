package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        validateUserDto(userDto);

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        User user = UserMapper.toEntity(userDto);
        user.setId(null);
        User newUser = userRepository.save(user);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!userDto.getEmail().contains("@")) {
                throw new ValidationException("Некорректный формат email");
            }

            userRepository.findByEmail(userDto.getEmail()).ifPresent(userWithEmail -> {
                if (!userWithEmail.getId().equals(id)) {
                    throw new ConflictException("Email " + userDto.getEmail() + " уже используется другим пользователем");
                }
            });

            existingUser.setEmail(userDto.getEmail());
        } else if (userDto.getEmail() != null && userDto.getEmail().isBlank()) {
            throw new ValidationException("Email не должен быть пустым");
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        } else if (userDto.getName() != null && userDto.getName().isBlank()) {
            throw new ValidationException("Имя не должно быть пустым");
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        userRepository.deleteById(id);
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Email не должен быть пустым");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный формат email");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Имя не должно быть пустым");
        }
    }

    private Optional<User> findByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}
