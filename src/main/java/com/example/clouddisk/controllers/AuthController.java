package com.example.clouddisk.controllers;


import com.example.clouddisk.dto.AuthDto;
import com.example.clouddisk.dto.UserDto;
import com.example.clouddisk.exceptions.user.MailAlreadyExist;
import com.example.clouddisk.exceptions.user.UsernameAlreadyExist;
import com.example.clouddisk.models.Disk;
import com.example.clouddisk.models.User;
import com.example.clouddisk.repos.DiskRepo;
import com.example.clouddisk.repos.UserRepo;
import com.example.clouddisk.security.jwt.JwtTokenProvider;
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

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
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

    public ResponseEntity authByToken(@RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);
        return new ResponseEntity(UserDto.fromUser(user),HttpStatus.OK);
    }
}
