package com.example.clouddisk.service;


import com.example.clouddisk.exceptions.user.MailAlreadyExist;
import com.example.clouddisk.exceptions.user.UserNotFoundException;
import com.example.clouddisk.exceptions.user.UsernameAlreadyExist;
import com.example.clouddisk.models.Role;
import com.example.clouddisk.models.User;
import com.example.clouddisk.repos.RoleRepo;
import com.example.clouddisk.repos.UserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileService fileService;

    public UserService(UserRepo userRepo, RoleRepo roleRepo, BCryptPasswordEncoder passwordEncoder, FileService fileService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
    }

    public User register(User user) throws UsernameAlreadyExist, MailAlreadyExist {
        if (userRepo.findByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyExist(String.format("User with name %s already exist", user.getUsername()));
        }
        if (userRepo.findByMail(user.getMail()) != null) {
            throw new MailAlreadyExist(String.format("User with mail %s already exist", user.getMail()));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Role> roles = Arrays.asList(roleRepo.findById(1L).get());
        user.setRoles(roles);

        fileService.createRoot(user);

        return userRepo.save(user);
    }

    public User findByUsername(String username) throws UserNotFoundException {
        User result = userRepo.findByUsername(username);
        if (result == null) {
            throw new UserNotFoundException("User not fount");
        }
        return result;
    }

    public User findById(Long id) throws UserNotFoundException {
        User result = userRepo.findById(id).orElse(null);
        if (result == null) {
            throw new UserNotFoundException("User not fount");
        }
        return result;
    }

    public List<User> getAll() {
        return userRepo.findAll();
    }
}
