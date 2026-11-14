package com.studentjob.service;

import com.studentjob.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    User registerUser(User user);
    boolean authenticate(String username, String password);
}
