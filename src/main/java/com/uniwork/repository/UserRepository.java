package com.uniwork.repository;

import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByRoleAndActive(Role role, Boolean active);
    long countByRole(Role role);
    long countByRoleAndActive(Role role, Boolean active);
}
