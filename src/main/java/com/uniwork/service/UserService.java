package com.uniwork.service;

import com.uniwork.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    User registerUser(User user);
    boolean authenticate(String username, String password);
}
