package com.example.clouddisk.dto;

import com.example.clouddisk.models.Role;
import com.example.clouddisk.models.User;
import com.example.clouddisk.service.file.Base64Encoder;
import com.example.clouddisk.service.file.FileService;
import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class UserDto {
    private String username;
    private Long id;
    private Long freeSpace;
    private String mail;
    private List<Role> roles;
    private String avatar;

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setMail(user.getMail());
        userDto.setFreeSpace(user.getFreeSpace());
        userDto.setRoles(user.getRoles());
        if (user.getHasAvatar()) {
            String img = FileService.FILE_PATH + user.getUsername() + "\\avatar.jpg";
            File imgFile = new File(img);
            if (imgFile.exists()) {
                userDto.setAvatar(Base64Encoder.encodeFileToBase64Binary(imgFile));
            }
        } else {
            String img = FileService.DEFAULT_AVATAR;
            System.out.println(FileService.DEFAULT_AVATAR);
            File imgFile = new File(img);
            if (imgFile.exists()) {
                userDto.setAvatar(Base64Encoder.encodeFileToBase64Binary(imgFile));
            }
        }
        return userDto;
    }
}
