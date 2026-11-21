package com.uniwork.controller;

import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.service.EmployerService;
import com.uniwork.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho AuthController (Phân hệ Xác thực & Phân quyền)
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test cho AuthController (Phụ trách: Cao Trần Duy Minh - PC11166)")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private EmployerService employerService;

    @InjectMocks
    private AuthController authController;

    private User studentUser;
    private User employerUser;

    @BeforeEach
    void setUp() {
        studentUser = User.builder()
                .id(1L)
                .username("sinhvien_cantho")
                .password("123456")
                .fullName("Nguyễn Văn Sinh Viên")
                .email("sinhvien@fpt.edu.vn")
                .role(Role.STUDENT)
                .active(true)
                .build();

        employerUser = User.builder()
                .id(2L)
                .username("highlandscantho")
                .password("123456")
                .fullName("Highlands Ninh Kiều")
                .email("ninhkieu@highlandscoffee.com.vn")
                .role(Role.COMPANY_ADMIN)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Kiểm tra mở trang đăng nhập thành công")
    void should_ReturnLoginPage() {
        Model model = new ConcurrentModel();
        String view = authController.loginPage(null, null, model);
        assertEquals("login", view);
    }

    @Test
    @DisplayName("Kiểm tra mở trang đăng nhập kèm thông báo đã đăng ký hoặc đăng xuất")
    void should_SetAttributes_When_RegisteredOrLogoutParamProvided() {
        Model model1 = new ConcurrentModel();
        authController.loginPage("true", null, model1);
        assertTrue(model1.containsAttribute("success"));

        Model model2 = new ConcurrentModel();
        authController.loginPage(null, "true", model2);
        assertTrue(model2.containsAttribute("info"));
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập thành công vai trò Sinh viên -> chuyển hướng thẳng sang Sàn việc làm (/jobs)")
    void should_RedirectToJobs_When_StudentLogsIn() {
        MockHttpSession session = new MockHttpSession();
        Model model = new ConcurrentModel();

        when(userService.authenticate("sinhvien_cantho", "123456")).thenReturn(true);
        when(userService.findByUsernameOrEmail("sinhvien_cantho")).thenReturn(Optional.of(studentUser));

        String view = authController.processLogin("sinhvien_cantho", "123456", session, model);

        assertEquals("redirect:/jobs", view);
        assertEquals(studentUser, session.getAttribute("currentUser"));
        assertEquals("STUDENT", session.getAttribute("userRole"));
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập thành công vai trò Company Admin -> chuyển hướng sang Trang Tổng Quan (Dashboard)")
    void should_RedirectToEmployerPortal_When_EmployerLogsIn() {
        MockHttpSession session = new MockHttpSession();
        Model model = new ConcurrentModel();

        when(userService.authenticate("highlandscantho", "123456")).thenReturn(true);
        when(userService.findByUsernameOrEmail("highlandscantho")).thenReturn(Optional.of(employerUser));

        String view = authController.processLogin("highlandscantho", "123456", session, model);

        assertEquals("redirect:/employer/dashboard", view);
        assertEquals(employerUser, session.getAttribute("currentUser"));
        assertEquals("COMPANY_ADMIN", session.getAttribute("userRole"));
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập với URL lưu sẵn từ interceptor -> chuyển hướng đến URL đã lưu")
    void should_RedirectToSavedUrl_When_RedirectAfterLoginPresent() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("redirectAfterLogin", "/employer/jobs/create");
        Model model = new ConcurrentModel();

        when(userService.authenticate("highlandscantho", "123456")).thenReturn(true);
        when(userService.findByUsernameOrEmail("highlandscantho")).thenReturn(Optional.of(employerUser));

        String view = authController.processLogin("highlandscantho", "123456", session, model);

        assertEquals("redirect:/employer/jobs/create", view);
        assertNull(session.getAttribute("redirectAfterLogin"));
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập thất bại khi thông tin không chính xác")
    void should_ReturnLoginWithError_When_AuthenticationFails() {
        MockHttpSession session = new MockHttpSession();
        Model model = new ConcurrentModel();

        when(userService.authenticate("sai_user", "123456")).thenReturn(false);

        String view = authController.processLogin("sai_user", "123456", session, model);

        assertEquals("login", view);
        assertTrue(model.containsAttribute("error"));
        assertNull(session.getAttribute("currentUser"));
    }

    @Test
    @DisplayName("Kiểm tra đăng ký tài khoản Sinh viên thành công")
    void should_RegisterStudent_Success() {
        Model model = new ConcurrentModel();

        when(userService.registerUser(any(User.class))).thenReturn(studentUser);

        String view = authController.processRegister(
                "Nguyễn Văn Sinh Viên", "sinhvien_cantho", "sinhvien@fpt.edu.vn",
                "0939123456", "123456", "123456", "STUDENT", model);

        assertEquals("redirect:/login?registered=true", view);
        verify(userService, times(1)).registerUser(any(User.class));
        verify(employerService, never()).getOrCreateEmployerForUser(any());
    }

    @Test
    @DisplayName("Kiểm tra đăng ký tài khoản Nhà tuyển dụng thành công và tạo trước hồ sơ doanh nghiệp")
    void should_RegisterEmployer_SuccessAndInitEmployerProfile() {
        Model model = new ConcurrentModel();

        when(userService.registerUser(any(User.class))).thenReturn(employerUser);

        String view = authController.processRegister(
                "Highlands Ninh Kiều", "highlandscantho", "ninhkieu@highlandscoffee.com.vn",
                "02923888999", "123456", "123456", "COMPANY_ADMIN", model);

        assertEquals("redirect:/login?registered=true", view);
        verify(userService, times(1)).registerUser(any(User.class));
        verify(employerService, times(1)).getOrCreateEmployerForUser(employerUser);
    }

    @Test
    @DisplayName("Kiểm tra đăng ký thất bại khi mật khẩu xác nhận không trùng khớp")
    void should_FailRegister_When_PasswordMismatch() {
        Model model = new ConcurrentModel();

        String view = authController.processRegister(
                "Tên", "username", "email@fpt.edu.vn",
                "0939123456", "123456", "654321", "STUDENT", model);

        assertEquals("register", view);
        assertTrue(model.containsAttribute("error"));
        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("Kiểm tra đăng ký thất bại khi mật khẩu quá ngắn (<6 ký tự)")
    void should_FailRegister_When_PasswordTooShort() {
        Model model = new ConcurrentModel();

        String view = authController.processRegister(
                "Tên", "username", "email@fpt.edu.vn",
                "0939123456", "123", "123", "STUDENT", model);

        assertEquals("register", view);
        assertTrue(model.containsAttribute("error"));
        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("Kiểm tra đăng xuất -> hủy phiên làm việc và chuyển về trang login")
    void should_InvalidateSessionAndRedirectToLogin_When_Logout() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", studentUser);

        String view = authController.processLogout(session);

        assertEquals("redirect:/login?logout=true", view);
        assertTrue(session.isInvalid());
    }
}
