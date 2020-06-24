package com.example.clouddisk.controllers;


import com.example.clouddisk.dto.CloudFileDto;
import com.example.clouddisk.models.CloudFile;
import com.example.clouddisk.models.User;
import com.example.clouddisk.service.UserService;
import com.example.clouddisk.service.file.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }




    @PostMapping
    public ResponseEntity createDirectory(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody CloudFileDto cloudFileDto) {
        User user = userService.getUserByToken(authHeader);
        return ResponseEntity.ok(CloudFileDto.fromCloudFile(fileService.saveDir(cloudFileDto, user)));
    }
}
