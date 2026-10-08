package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        if (userStorage.existsByEmail(userDto.getEmail(), null)) {
            throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }
        User user = UserMapper.toUser(userDto);
        User createdUser = userStorage.create(user);
        log.info("Создан пользователь с id {}", createdUser.getId());
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User existingUser = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
                throw new ValidationException("Некорректный адрес электронной почты: " + userDto.getEmail());
            }
            if (userStorage.existsByEmail(userDto.getEmail(), userId)) {
                throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        User updatedUser = userStorage.update(existingUser);
        log.info("Обновлен пользователь с id {}", updatedUser.getId());
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        userStorage.delete(userId);
        log.info("Удален пользователь с id {}", userId);
    }
}