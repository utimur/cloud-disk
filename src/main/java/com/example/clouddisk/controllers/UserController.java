package com.example.clouddisk.controllers;


import com.example.clouddisk.models.User;
import com.example.clouddisk.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity setAvatar(@RequestHeader("Authorization") String authHeader,
                                    @RequestParam(value = "img") MultipartFile img) throws IOException {

        User user = userService.getUserByToken(authHeader);
        userService.saveAvatar(img, user);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
