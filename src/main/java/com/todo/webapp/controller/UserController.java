package com.todo.webapp.controller;

import com.todo.webapp.dto.UserResponseDto;
import com.todo.webapp.service.AuthenticationService;
import com.todo.webapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createNewUserAccount(@RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(authenticationService.addNewUser(accessToken));
    }


}
