package com.example.clouddisk.controllers;


import com.example.clouddisk.dto.AuthDto;
import com.example.clouddisk.dto.UserDto;
import com.example.clouddisk.exceptions.user.*;
import com.example.clouddisk.models.Disk;
import com.example.clouddisk.models.User;
import com.example.clouddisk.repos.DiskRepo;
import com.example.clouddisk.repos.UserRepo;
import com.example.clouddisk.security.jwt.JwtTokenProvider;
import com.example.clouddisk.service.MailSender;
import com.example.clouddisk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepo userRepo;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepo userRepo, MailSender mailSender) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepo = userRepo;
    }



    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) throws UsernameAlreadyExist, MailAlreadyExist {
        return ResponseEntity.ok(UserDto.fromUser(userService.register(user)));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthDto authDto) {
        Boolean sendActivationAgain = authDto.isNeedSendActivation();
        HashMap<Object,Object> response = new HashMap<>();
        try {
            String username = authDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, authDto.getPassword()));
            User user = userService.findByUsername(username);

            if (user.getIsActivated() || sendActivationAgain) {
                if (sendActivationAgain) {
                    userService.sendActivationEmail(user);
                    response.put("user", UserDto.fromUser(user));
                    return ResponseEntity.ok(response);
                }
                String token = jwtTokenProvider.createToken(username, user.getRoles(), user.getId(), false);
                response.put("token", token);
                response.put("user", UserDto.fromUser(user));
                // Используется для того, чтобы снова отправить письмо с активацией

                return ResponseEntity.ok(response);
            }
            else {
                response.put("error", UserIsNotActivatedException.getException());
                Boolean needSendActivation = userService.checkPeriodActivation(user.getSentActivationAt().getTime());
                response.put("needSendActivation", needSendActivation);

                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
            }
        } catch (AuthenticationException e) {
            response.put("error", UserNotFoundException.getException());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    @GetMapping("/activation")
    public ResponseEntity activation(@RequestHeader("Authorization") String authHeader) {
        User user = userService.getUserByToken(authHeader);
        HashMap<Object,Object> response = new HashMap<>();
        if (user == null)
        {
            response.put("error", UserNotFoundException.getException());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (!user.getIsActivated()) {
            user.setIsActivated(true);
            userRepo.save(user);
            return ResponseEntity.ok(UserDto.fromUser(user));
        }
        response.put("error", UserIsAlreadyActivatedException.getException());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping
    public ResponseEntity authByToken(@RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);
        String newToken = jwtTokenProvider.createToken(user.getUsername(), user.getRoles(), user.getId(), false);

        Map<Object, Object> response = new HashMap<>();
        response.put("token", newToken);
        response.put("user", UserDto.fromUser(user));

        return new ResponseEntity(response, HttpStatus.OK);
    }
}
