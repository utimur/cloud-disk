package com.example.clouddisk.controllers;


import com.example.clouddisk.dto.CloudFileDto;
import com.example.clouddisk.models.CloudFile;
import com.example.clouddisk.models.User;
import com.example.clouddisk.service.UserService;
import com.example.clouddisk.service.file.Base64Encoder;
import com.example.clouddisk.service.file.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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




    @PostMapping("/dir")
    public ResponseEntity createDirectory(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody CloudFileDto cloudFileDto) {
        if (cloudFileDto.getParentId() == 0) {
            cloudFileDto.setParentId(null);
        }
        User user = userService.getUserByToken(authHeader);
        return ResponseEntity.ok(CloudFileDto.fromCloudFile(fileService.saveDir(cloudFileDto, user)));
    }


    @PostMapping("/upload")
    public ResponseEntity uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart String filename,
            @RequestParam(value = "parent_id", required = false) Long parentId,
            @RequestHeader("Authorization") String token) throws IOException {

        User user = userService.getUserByToken(token);
        CloudFile cloudFile;

        cloudFile = fileService.saveFile(file, filename, user, parentId);

        return new ResponseEntity(CloudFileDto.fromCloudFile(cloudFile), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getFiles(@RequestHeader("Authorization") String token,
                                   @RequestParam(value = "parent_id", required = false) Long parentId) {
        User user = userService.getUserByToken(token);
        Map<Object, Object> response = new HashMap<>();
        response.put("files", fileService.getByParentIdAndDiskId(parentId, user.getDisk().getId())
                .stream()
                .map(CloudFileDto::fromCloudFile)
                .collect(Collectors.toList()));
        if(parentId == null) {
            response.put("backId", null);
        } else {
            CloudFile file = fileService.getById(parentId);
            if ( file.getParent() == null) {
                response.put("backId", null);
            } else {
                response.put("backId", file.getParent().getId());
            }
        }

        return ResponseEntity.ok(response);
    }
}
