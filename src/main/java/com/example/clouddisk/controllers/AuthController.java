package com.example.clouddisk.controllers;


import com.example.clouddisk.dto.AuthDto;
import com.example.clouddisk.dto.UserDto;
import com.example.clouddisk.exceptions.user.MailAlreadyExist;
import com.example.clouddisk.exceptions.user.UserIsAlreadyActivatedException;
import com.example.clouddisk.exceptions.user.UsernameAlreadyExist;
import com.example.clouddisk.models.User;
import com.example.clouddisk.repos.UserRepo;
import com.example.clouddisk.security.jwt.JwtTokenProvider;
import com.example.clouddisk.service.MailSender;
import com.example.clouddisk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        try {
            String username = authDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, authDto.getPassword()));
            User user = userService.findByUsername(username);

            String token = jwtTokenProvider.createToken(username, user.getRoles(), user.getId());

            Map<Object, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", UserDto.fromUser(user));

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @GetMapping("/activation")
    public ResponseEntity activation(@RequestHeader("Authorization") String authHeader){
        System.out.println(authHeader);
        User user = userService.getUserByToken(authHeader);
        if (user == null) throw new UsernameNotFoundException("User not found");
        if (!user.getIsActivated()){
            user.setIsActivated(true);
            userRepo.save(user);
            return ResponseEntity.ok(UserDto.fromUser(user));
        }
        throw new UserIsAlreadyActivatedException("User is already activated");

    }
}
