package com.example.clouddisk.service;


import com.example.clouddisk.exceptions.user.MailAlreadyExist;
import com.example.clouddisk.exceptions.user.UserNotFoundException;
import com.example.clouddisk.exceptions.user.UsernameAlreadyExist;
import com.example.clouddisk.models.Basket;
import com.example.clouddisk.models.Disk;
import com.example.clouddisk.exceptions.user.*;
import com.example.clouddisk.models.Role;
import com.example.clouddisk.models.User;
import com.example.clouddisk.repos.BasketRepo;
import com.example.clouddisk.repos.DiskRepo;
import com.example.clouddisk.repos.RoleRepo;
import com.example.clouddisk.repos.UserRepo;
import com.example.clouddisk.security.jwt.JwtTokenProvider;
import com.example.clouddisk.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiskRepo diskRepo;
    private final BasketRepo basketRepo;
    private final MailSender mailSender;

    @Value("${jwt.token.activation.expired}")
    private long validityActivationInMillSeconds;

    public UserService(UserRepo userRepo, RoleRepo roleRepo, BCryptPasswordEncoder passwordEncoder, FileService fileService, JwtTokenProvider jwtTokenProvider, DiskRepo diskRepo, BasketRepo basketRepo, MailSender mailSender) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.diskRepo = diskRepo;
        this.basketRepo = basketRepo;
        this.mailSender = mailSender;
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
        //В этой функции и сохраняется user
        sendActivationEmail(user);


        Disk disk = new Disk();
        Basket basket = new Basket();

        disk.setUser(user);
        basket.setUser(user);

        diskRepo.save(disk);
        basketRepo.save(basket);

        fileService.createRoot(user);

        return user;
    }

    public void sendActivationEmail(User user) {
        // Отправка email активации на почту
        String text = "Hello, " + user.getUsername() + ". Click on link to activate your account " +  "http://localhost:3000/auth/activation/" + jwtTokenProvider.createToken(user.getUsername(), user.getRoles(), user.getId(), true);
        mailSender.sendMessage(user.getMail(), "Cloud Store. Account activation.", text);
        user.setSentActivationAt(new Date());
        userRepo.save(user);
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

    public User getUserByToken(String token) {
        String authToken = token.substring(7);
        return userRepo.findByUsername(jwtTokenProvider.getUsername(authToken));
    }

    public User saveAvatar(MultipartFile img, User user) throws IOException {
        if(img != null) {
            String fileName = "avatar.jpg";
            File convertFile = new File(FileService.FILE_PATH + user.getUsername() + "\\" + fileName);
            convertFile.createNewFile();
            FileOutputStream fout = new FileOutputStream(convertFile);
            fout.write(img.getBytes());
            fout.close();
        }
        user.setHasAvatar(true);
        return userRepo.save(user);
    }

    public void changePassword(User user, String newPassword){
       if (newPassword.equals("") || newPassword.isEmpty()) throw new EmptyPasswordException("Password is empty");
       if (newPassword.length() < 5) throw new ShortPasswordException("Password is short, length must be more than 4");
       user.setPassword(passwordEncoder.encode(newPassword));
       userRepo.save(user);
    }

    public User update(User user) {
        return userRepo.save(user);
    }

    // Вернёт true, если после отправления активации прошло более 30 дней
    // time - дата, когда было отправлено письмо активации
    public boolean checkPeriodActivation(Long time) {
        Date currentDate = new Date();
        return time + validityActivationInMillSeconds <= currentDate.getTime();
    }
}
