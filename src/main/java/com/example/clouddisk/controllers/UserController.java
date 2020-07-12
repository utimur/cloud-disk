package com.example.clouddisk.controllers;


import com.example.clouddisk.dto.AuthDto;
import com.example.clouddisk.dto.UserDto;
import com.example.clouddisk.models.User;
import com.example.clouddisk.service.UserService;
import com.example.clouddisk.service.file.Base64Encoder;
import com.sun.mail.util.BASE64EncoderStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity setAvatar(
            @RequestPart("img") MultipartFile img,
            @RequestPart String filename,
            @RequestHeader("Authorization") String authHeader
                                    ) throws IOException {

        User user = userService.getUserByToken(authHeader);
        File avatar = userService.saveAvatar(img, user);
        HashMap<Object,Object> response = new HashMap<>();
        if (avatar != null) {
            response.put("avatar", Base64Encoder.encodeFileToBase64Binary(avatar));
            return ResponseEntity.ok(response);
        }
        response.put("error", "NullPointerException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PostMapping("/password")
    public ResponseEntity changeUserPassword(@RequestHeader("Authorization") String authHeader,
                                             @RequestBody AuthDto authDto){
        User user = userService.getUserByToken(authHeader);
        userService.changePassword(user, authDto.getPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
