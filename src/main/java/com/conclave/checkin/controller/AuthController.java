package com.conclave.checkin.controller;

import com.conclave.checkin.dto.LoginRequest;
import com.conclave.checkin.model.User;
import com.conclave.checkin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository repository;

    @PostMapping("/login")
    public String login(
            @RequestBody
            LoginRequest request
    ) {

        User user =
                repository
                        .findByUsername(
                                request.getUsername()
                        )
                        .orElse(null);

        if (
                user == null
                        ||
                        !user.getPassword()
                                .equals(
                                        request.getPassword()
                                )
        ) {

            return "INVALID";
        }

        return "SUCCESS";
    }
}