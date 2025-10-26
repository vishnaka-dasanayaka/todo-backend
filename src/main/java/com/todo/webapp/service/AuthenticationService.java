package com.todo.webapp.service;

import com.todo.webapp.dto.UserResponseDto;
import com.todo.webapp.entity.User;
import com.todo.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

import java.sql.Timestamp;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;


    public UserResponseDto addNewUser(String token) {
        GetUserResponse getUserResponse = getUserDetails(token);

        String email = getAttribute(getUserResponse, "email");
        String cognitoSub = getAttribute(getUserResponse,"sub");
        String firstName = getAttribute(getUserResponse,"given_name");
        String lastName = getAttribute(getUserResponse,"family_name");

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        }

        User newUser = new User(
                email,
                firstName,
                lastName,
                cognitoSub);

        userRepository.save(newUser);
        return new UserResponseDto(true,getUserResponse.username());
    }

    private GetUserResponse getUserDetails(String accessToken) {
        GetUserRequest request = GetUserRequest.builder()
                .accessToken(accessToken)
                .build();

        return cognitoIdentityProviderClient.getUser(request);
    }

    private String getAttribute(GetUserResponse user, String attributeName) {
        return user.userAttributes().stream()
                .filter(attr -> attributeName.equals(attr.name()))
                .map(attr -> attr.value())
                .findFirst()
                .orElse("Not Found");
    }
}