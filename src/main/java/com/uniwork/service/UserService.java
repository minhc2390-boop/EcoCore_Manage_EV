package com.uniwork.service;

import com.uniwork.entity.Role;
import com.uniwork.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service quản lý người dùng và xác thực tài khoản
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String identifier);
    Optional<User> findById(Long id);
    User registerUser(User user);
    boolean authenticate(String identifier, String password);
    List<User> getUsersByRole(Role role);
    List<User> searchUsers(Role role, String status, String keyword);
    User toggleUserStatus(Long id);
    User resetUserPassword(Long id, String newPassword);
    long countUsersByRole(Role role);
    long countUsersByRoleAndStatus(Role role, Boolean active);
}
