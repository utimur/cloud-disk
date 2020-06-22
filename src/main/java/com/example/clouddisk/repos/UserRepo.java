package com.example.clouddisk.repos;

import com.example.clouddisk.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {
    User findByUsername(String username);

    User findByMail(String mail);

    List<User> findAll();
}
