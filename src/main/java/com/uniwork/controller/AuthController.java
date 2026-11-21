package com.uniwork.controller;

import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.service.EmployerService;
import com.uniwork.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Controller xử lý xác thực, đăng nhập, đăng ký và phân quyền hệ thống UniWork
 * @author Cao Trần Duy Minh - PC11166 (Nghiệp vụ Đăng ký & Đăng nhập Hệ thống UniWork)
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final EmployerService employerService;

    // Giao diện đăng nhập
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "registered", required = false) String registered,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (registered != null) {
            model.addAttribute("success", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
        }
        if (logout != null) {
            model.addAttribute("info", "Bạn đã đăng xuất khỏi hệ thống!");
        }
        return "login";
    }

    // Xử lý form đăng nhập (hỗ trợ cả Tên đăng nhập hoặc Email)
    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        // Kiểm tra thông tin xác thực
        boolean isValid = userService.authenticate(username, password);
        if (!isValid) {
            model.addAttribute("error", "Tên đăng nhập / Email hoặc mật khẩu không chính xác!");
            model.addAttribute("username", username);
            return "login";
        }

        // Lấy đối tượng User đã xác thực thành công
        Optional<User> userOpt = userService.findByUsernameOrEmail(username);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Tài khoản không tồn tại trên hệ thống!");
            return "login";
        }
        User currentUser = userOpt.get();

        // Lưu thông tin người dùng vào session
        session.setAttribute("currentUser", currentUser);
        session.setAttribute("userRole", currentUser.getRole().name());
        log.info("Người dùng '{}' đăng nhập thành công với vai trò {}", currentUser.getUsername(),
                currentUser.getRole());

        // Chuyển hướng đến URL đã lưu trước đó nếu có
        String redirectAfterLogin = (String) session.getAttribute("redirectAfterLogin");
        if (redirectAfterLogin != null && !redirectAfterLogin.isBlank()) {
            session.removeAttribute("redirectAfterLogin");
            return "redirect:" + redirectAfterLogin;
        }

        // Phân luồng mặc định theo vai trò tài khoản
        if (currentUser.getRole() == Role.COMPANY_ADMIN || currentUser.getRole() == Role.SUPER_ADMIN) {
            return "redirect:/employer/jobs";
        } else {
            // Sinh viên đăng nhập thành công sẽ được chuyển thẳng tới Sàn việc làm (/jobs) để tìm việc & ứng tuyển
            return "redirect:/jobs";
        }
    }

    // Giao diện đăng ký tài khoản mới
    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    // Xử lý gửi biểu mẫu đăng ký
    @PostMapping("/register")
    public String processRegister(@RequestParam("fullName") String fullName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam(value = "role", defaultValue = "STUDENT") String roleStr,
            Model model) {
        // Kiểm tra tính hợp lệ của mật khẩu xác nhận
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không trùng khớp!");
            preserveFormData(model, fullName, username, email, phone, roleStr);
            return "register";
        }
        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có độ dài tối thiểu là 6 ký tự trở lên!");
            preserveFormData(model, fullName, username, email, phone, roleStr);
            return "register";
        }

        // Xác định vai trò tài khoản đăng ký
        Role userRole = Role.STUDENT;
        if ("COMPANY_ADMIN".equalsIgnoreCase(roleStr) || "employer".equalsIgnoreCase(roleStr)) {
            userRole = Role.COMPANY_ADMIN;
        }

        // Khởi tạo đối tượng User mới
        User newUser = User.builder()
                .fullName(fullName.trim())
                .username(username.trim())
                .email(email.trim().toLowerCase())
                .phone(phone != null && !phone.isBlank() ? phone.trim() : null)
                .password(password)
                .role(userRole)
                .active(true)
                .build();

        try {
            User savedUser = userService.registerUser(newUser);

            // Tự động khởi tạo trước hồ sơ doanh nghiệp nếu đăng ký vai trò Nhà tuyển dụng
            if (savedUser.getRole() == Role.COMPANY_ADMIN) {
                try {
                    employerService.getOrCreateEmployerForUser(savedUser);
                } catch (Exception ex) {
                    log.warn("Không thể tự động tạo hồ sơ Doanh nghiệp cho '{}': {}", savedUser.getUsername(), ex.getMessage());
                }
            }

            log.info("Đăng ký thành công tài khoản mới: {} ({})", savedUser.getUsername(), savedUser.getRole());
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException ex) {
            // Lỗi trùng tên đăng nhập hoặc email
            model.addAttribute("error", ex.getMessage());
            preserveFormData(model, fullName, username, email, phone, roleStr);
            return "register";
        } catch (Exception ex) {
            log.error("Lỗi khi đăng ký tài khoản: ", ex);
            model.addAttribute("error", "Đã xảy ra lỗi trong quá trình tạo tài khoản. Vui lòng thử lại!");
            preserveFormData(model, fullName, username, email, phone, roleStr);
            return "register";
        }
    }

    // Đăng xuất khỏi hệ thống
    @GetMapping("/logout")
    public String processLogout(HttpSession session) {
        if (session != null) {
            session.invalidate(); // Xóa sạch toàn bộ phiên đăng nhập
        }
        return "redirect:/login?logout=true";
    }

    // Giữ lại dữ liệu người dùng đã nhập khi biểu mẫu phát sinh lỗi
    private void preserveFormData(Model model, String fullName, String username, String email, String phone, String role) {
        model.addAttribute("fullName", fullName);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("role", role);
    }
}
