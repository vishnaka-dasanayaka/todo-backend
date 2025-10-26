package com.todo.webapp.controller;

import com.todo.webapp.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<String> createNewUserAccount(@RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(authenticationService.addNewUser(accessToken));
    }
}
