package com.uniwork.service.impl;

import com.uniwork.entity.User;
import com.uniwork.repository.UserRepository;
import com.uniwork.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Triển khai nghiệp vụ người dùng và xác thực tài khoản
 * Hỗ trợ đăng nhập linh hoạt bằng cả Tên đăng nhập (Username) hoặc Email
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return Optional.empty();
        }
        String clean = identifier.trim();
        Optional<User> byUser = userRepository.findByUsername(clean);
        if (byUser.isPresent()) {
            return byUser;
        }
        return userRepository.findByEmail(clean.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại trong hệ thống!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Địa chỉ email đã được đăng ký!");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String identifier, String password) {
        if (identifier == null || password == null) {
            return false;
        }
        return findByUsernameOrEmail(identifier)
                .map(user -> user.getPassword().equals(password) && Boolean.TRUE.equals(user.getActive()))
                .orElse(false);
    }
}
