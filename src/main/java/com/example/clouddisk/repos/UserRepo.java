package com.example.clouddisk.repos;

import com.example.clouddisk.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Long> {
}
