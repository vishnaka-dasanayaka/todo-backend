package com.todo.webapp.service;

import com.todo.webapp.entity.User;
import com.todo.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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


    public String addNewUser(String token) {
        GetUserResponse getUserResponse = getUserDetails(token);

        String givenName = getAttribute(getUserResponse, "given_name");
        String familyName = getAttribute(getUserResponse, "family_name");
        String phoneNumber = getAttribute(getUserResponse, "phone_number");
        String role = getAttribute(getUserResponse, "custom:role");
        String email = getAttribute(getUserResponse, "email");
        String cognitoSub = getAttribute(getUserResponse,"username");




        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());

        User newUser = new User(
                email,
                "familyName",
                "lastname",
                cognitoSub);

        userRepository.createUser(newUser);
        return getUserResponse.username();
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
