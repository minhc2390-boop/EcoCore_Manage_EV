package com.uniwork.service.impl;

import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.repository.UserRepository;
import com.uniwork.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    @Transactional(readOnly = true)
    public java.util.List<User> getUsersByRole(com.uniwork.entity.Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<User> searchUsers(com.uniwork.entity.Role role, String status, String keyword) {
        java.util.List<User> list = userRepository.findByRole(role);
        return list.stream()
                .filter(u -> {
                    if ("ACTIVE".equalsIgnoreCase(status)) {
                        return Boolean.TRUE.equals(u.getActive());
                    } else if ("LOCKED".equalsIgnoreCase(status)) {
                        return Boolean.FALSE.equals(u.getActive());
                    }
                    return true;
                })
                .filter(u -> {
                    if (keyword == null || keyword.isBlank()) {
                        return true;
                    }
                    String kw = keyword.trim().toLowerCase();
                    boolean matchUsername = u.getUsername() != null && u.getUsername().toLowerCase().contains(kw);
                    boolean matchName = u.getFullName() != null && u.getFullName().toLowerCase().contains(kw);
                    boolean matchEmail = u.getEmail() != null && u.getEmail().toLowerCase().contains(kw);
                    boolean matchPhone = u.getPhone() != null && u.getPhone().contains(kw);
                    boolean matchCompany = u.getCompany() != null && u.getCompany().getCompanyName() != null
                            && u.getCompany().getCompanyName().toLowerCase().contains(kw);
                    return matchUsername || matchName || matchEmail || matchPhone || matchCompany;
                })
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .toList();
    }

    @Override
    public User toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));
        boolean current = Boolean.TRUE.equals(user.getActive());
        user.setActive(!current);
        return userRepository.save(user);
    }

    @Override
    public User resetUserPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));
        user.setPassword(newPassword != null ? newPassword : "123456");
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(com.uniwork.entity.Role role) {
        return userRepository.countByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByRoleAndStatus(com.uniwork.entity.Role role, Boolean active) {
        return userRepository.countByRoleAndActive(role, active);
    }
}
