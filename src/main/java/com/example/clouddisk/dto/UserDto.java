package com.example.clouddisk.dto;

import com.example.clouddisk.models.Role;
import com.example.clouddisk.models.User;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String username;
    private Long id;
    private Long freeSpace;
    private String mail;
    private List<Role> roles;

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setMail(user.getMail());
        userDto.setFreeSpace(user.getFreeSpace());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}
