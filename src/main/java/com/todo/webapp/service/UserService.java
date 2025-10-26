package com.todo.webapp.service;

import com.todo.webapp.dto.UserDto;
import com.todo.webapp.entity.User;
import com.todo.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public UserDto createUser(UserDto dto){
        User user = User.builder()
                .email(dto.getEmail())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .build();

        User saved = userRepository.save(user);

        return toDto(saved);
    }

    private UserDto toDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }


}
