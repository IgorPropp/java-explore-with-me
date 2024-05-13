package ru.practicum.mapper;

import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public static User toUser(NewUserRequest userRequest) {
        return new User(userRequest.getEmail(), userRequest.getName());
    }

    public static List<UserDto> toListUserDto(List<User> list) {
        List<UserDto> listDto = new ArrayList<>();
        for (User user : list) {
            listDto.add(toUserDto(user));
        }
        return listDto;
    }
}