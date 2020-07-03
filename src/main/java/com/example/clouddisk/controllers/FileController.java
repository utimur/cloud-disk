package com.example.clouddisk.controllers;


import com.example.clouddisk.dto.CloudFileDto;
import com.example.clouddisk.dto.UserDto;
import com.example.clouddisk.exceptions.user.FullSpaceException;
import com.example.clouddisk.models.CloudFile;
import com.example.clouddisk.models.User;
import com.example.clouddisk.service.UserService;
import com.example.clouddisk.service.file.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
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
        User user = userService.getUserByToken(authHeader);
        return ResponseEntity.ok(CloudFileDto.fromCloudFile(fileService.saveDir(cloudFileDto, user)));
    }


    @PostMapping("/upload")
    public ResponseEntity uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart String filename,
            @RequestParam(value = "parent_id", required = false) Long parentId,
            @RequestHeader("Authorization") String token) throws IOException, FullSpaceException {

        User user = userService.getUserByToken(token);
        CloudFile cloudFile;
        user.setFreeSpace(user.getFreeSpace()+file.getSize());
        userService.update(user);
        cloudFile = fileService.saveFile(file, filename, user, parentId);
        Map<Object, Object> response = new HashMap<>();
        response.put("file", CloudFileDto.fromCloudFile(cloudFile));
        response.put("user", UserDto.fromUser(user));

        return new ResponseEntity(response, HttpStatus.OK);
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
        return getResponseFilesEntity(parentId, user, response);
    }

    @GetMapping("/order/type")
    public ResponseEntity getFilesOrderByName(@RequestHeader("Authorization") String token,
                                              @RequestParam(value = "parent_id", required = false) Long parentId,
                                              @RequestParam(defaultValue = "false") Boolean desc) {
        User user = userService.getUserByToken(token);
        Map<Object, Object> response = new HashMap<>();
        List<CloudFile> files = fileService.getByParentIdAndDiskIdOrderByType(parentId, user.getDisk().getId(), desc);
        response.put("files", files.stream()
                .map(CloudFileDto::fromCloudFile)
                .collect(Collectors.toList()));

        return getResponseFilesEntity(parentId, user, response);
    }

    @GetMapping("/order/name")
    public ResponseEntity getFilesOrderByType(@RequestHeader("Authorization") String token,
                                              @RequestParam(value = "parent_id", required = false) Long parentId,
                                              @RequestParam(defaultValue = "false") Boolean desc) {
        User user = userService.getUserByToken(token);
        Map<Object, Object> response = new HashMap<>();
        List<CloudFile> files = fileService.getByParentIdAndDiskIdOrderByName(parentId,user.getDisk().getId(),desc);
        response.put("files", files.stream()
                .map(CloudFileDto::fromCloudFile)
                .collect(Collectors.toList()));

        return getResponseFilesEntity(parentId, user, response);
    }

    @GetMapping("/order/date")
    public ResponseEntity getFilesOrderByDate(@RequestHeader("Authorization") String token,
                                              @RequestParam(value = "parent_id", required = false) Long parentId,
                                              @RequestParam(defaultValue = "false") Boolean desc) {
        HashMap<Object,Object> response = new HashMap<>();
        User user = userService.getUserByToken(token);
        List<CloudFile> cloudFiles = fileService.getByParentIdAndDiskIdOrderByDate(parentId, user.getDisk().getId(), desc);
        response.put("files", cloudFiles.stream()
                .map(CloudFileDto::fromCloudFile)
                .collect(Collectors.toList())
        );
        return getResponseFilesEntity(parentId, user, response);

    }

    private ResponseEntity getResponseFilesEntity(@RequestParam(value = "parent_id", required = false) Long parentId, User user, Map<Object, Object> response) {
        if(parentId == null) {
            response.put("backId", null);
            response.put("path", user.getUsername()+"\\disk\\" );
        } else {
            response.put("path", fileService.getFullUserDiskPath(fileService.getById(parentId), user).substring(26));
            CloudFile file = fileService.getById(parentId);
            if ( file.getParent() == null) {
                response.put("backId", null);
            } else {
                response.put("backId", file.getParent().getId());
            }
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    public ResponseEntity downloadFile(@RequestHeader("Authorization") String token,
                                       @RequestParam("file_id") Long fileId) throws MalformedURLException {
        CloudFile cloudFile = fileService.getById(fileId);
        User user = userService.getUserByToken(token);
        Resource resource = fileService.downloadFile(cloudFile, user);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("multipart/form-data"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping
    public ResponseEntity deleteFile(@RequestHeader("Authorization") String token,
                                     @RequestParam("file_id") Long fileId) throws IOException {
        CloudFile cloudFile = fileService.getById(fileId);
        User user = userService.getUserByToken(token);
        fileService.deleteFile(cloudFile,user);
        user.setFreeSpace(user.getFreeSpace()-cloudFile.getSize());
        userService.update(user);
        return ResponseEntity.ok("DELETE");
    }

    @GetMapping("/search/name")
    public ResponseEntity searchFilesByName(@RequestHeader("Authorization") String token,
                                            @RequestParam String name) {
        User user = userService.getUserByToken(token);
        Map<Object, Object> response = new HashMap<>();
        response.put("files",fileService.searchByName(user.getDisk().getId(), name).stream()
                .map(CloudFileDto::fromCloudFile).collect(Collectors.toList()));
        response.put("backId", null);
        response.put("path", user.getUsername()+"\\disk\\" );
        return ResponseEntity.ok(response);
    }
}
