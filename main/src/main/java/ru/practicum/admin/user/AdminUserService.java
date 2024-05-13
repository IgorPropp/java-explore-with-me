package ru.practicum.admin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserStorage userRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from, size);
        if (ids == null || ids.isEmpty()) {
            return UserMapper.toListUserDto(userRepository.findAll(pageable).getContent());
        } else {
            return UserMapper.toListUserDto(userRepository.findById(ids, pageable).getContent());
        }
    }


    @Transactional
    public UserDto addUser(NewUserRequest userRequest) {
        User user = UserMapper.toUser(userRequest);
        userRepository.saveAndFlush(user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}