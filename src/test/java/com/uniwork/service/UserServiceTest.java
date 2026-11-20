package com.uniwork.service;

import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.repository.UserRepository;
import com.uniwork.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho UserService (Phân hệ Quản lý người dùng)
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test cho UserService (Phụ trách: Cao Trần Duy Minh - PC11166)")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("minh_lead")
                .password("123456")
                .fullName("Cao Trần Duy Minh")
                .email("minh@uniwork.edu.vn")
                .phone("0939123456")
                .role(Role.SUPER_ADMIN)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập thành công với username và password chính xác")
    void should_AuthenticateSuccess_When_ValidCredentials() {
        when(userRepository.findByUsername("minh_lead")).thenReturn(Optional.of(sampleUser));

        boolean result = userService.authenticate("minh_lead", "123456");

        assertTrue(result);
        verify(userRepository, times(1)).findByUsername("minh_lead");
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập thành công với email và password chính xác")
    void should_AuthenticateSuccess_When_ValidEmailCredentials() {
        when(userRepository.findByUsername("minh@uniwork.edu.vn")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("minh@uniwork.edu.vn")).thenReturn(Optional.of(sampleUser));

        boolean result = userService.authenticate("minh@uniwork.edu.vn", "123456");

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("minh@uniwork.edu.vn");
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập thất bại khi nhập sai mật khẩu")
    void should_FailAuthentication_When_InvalidPassword() {
        when(userRepository.findByUsername("minh_lead")).thenReturn(Optional.of(sampleUser));

        boolean result = userService.authenticate("minh_lead", "sai_mat_khau");

        assertFalse(result);
        verify(userRepository, times(1)).findByUsername("minh_lead");
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập thất bại khi tài khoản không tồn tại")
    void should_FailAuthentication_When_UserNotFound() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("unknown_user")).thenReturn(Optional.empty());

        boolean result = userService.authenticate("unknown_user", "123456");

        assertFalse(result);
    }

    @Test
    @DisplayName("Kiểm tra tìm kiếm tài khoản theo username hoặc email")
    void should_FindByUsernameOrEmail_Success() {
        when(userRepository.findByUsername("minh_lead")).thenReturn(Optional.of(sampleUser));
        when(userRepository.findByUsername("minh@uniwork.edu.vn")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("minh@uniwork.edu.vn")).thenReturn(Optional.of(sampleUser));

        Optional<User> byUser = userService.findByUsernameOrEmail("minh_lead");
        Optional<User> byEmail = userService.findByUsernameOrEmail("minh@uniwork.edu.vn");

        assertTrue(byUser.isPresent());
        assertEquals("minh_lead", byUser.get().getUsername());
        assertTrue(byEmail.isPresent());
        assertEquals("minh@uniwork.edu.vn", byEmail.get().getEmail());
    }

    @Test
    @DisplayName("Kiểm tra đăng ký tài khoản thành công khi dữ liệu hợp lệ")
    void should_RegisterUser_When_DataValid() {
        when(userRepository.existsByUsername("minh_lead")).thenReturn(false);
        when(userRepository.existsByEmail("minh@uniwork.edu.vn")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User savedUser = userService.registerUser(sampleUser);

        assertNotNull(savedUser);
        assertEquals("minh_lead", savedUser.getUsername());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Kiểm tra ném ngoại lệ khi tên đăng nhập đã tồn tại")
    void should_ThrowException_When_UsernameAlreadyExists() {
        when(userRepository.existsByUsername("minh_lead")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(sampleUser);
        });

        assertEquals("Tên đăng nhập đã tồn tại trong hệ thống!", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Kiểm tra ném ngoại lệ khi địa chỉ email đã tồn tại")
    void should_ThrowException_When_EmailAlreadyExists() {
        when(userRepository.existsByUsername("minh_lead")).thenReturn(false);
        when(userRepository.existsByEmail("minh@uniwork.edu.vn")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(sampleUser);
        });

        assertEquals("Địa chỉ email đã được đăng ký!", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
