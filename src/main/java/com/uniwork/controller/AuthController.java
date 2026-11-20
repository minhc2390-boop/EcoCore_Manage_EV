package com.uniwork.controller;

import com.uniwork.entity.Role;
import com.uniwork.entity.User;
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
 * Controller xử lý xác thực, đăng nhập, đăng ký và phân quyền
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;

    // đăng nhập
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

    // Xử lý form đăng nhập
    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        // kiểm tra xác thực qua service
        boolean isValid = userService.authenticate(username, password);
        if (!isValid) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            model.addAttribute("username", username);
            return "login";
        }
        // Lấy thông tin user đăng nhập thành công
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Tài khoản không tồn tại trên hệ thống!");
            return "login";
        }
        User currentUser = userOpt.get();

        // lưu thông tin của người dùng vào session
        session.setAttribute("currentUser", currentUser);
        session.setAttribute("userRole", currentUser.getRole().name());
        log.info("Người dùng '{}' đăng nhập thành công với vai trò {}", currentUser.getUsername(),
                currentUser.getRole());

        // phân hướng người dùng theo vai trò
        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            return "redirect:/";
        } else if (currentUser.getRole() == Role.COMPANY_ADMIN) {
            return "redirect:/";
        } else {
            return "redirect:/";
        }
    }

    // form đăng ký
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // xử lý form đăng ký account mới
    @PostMapping("/register")
    public String processRegister(@RequestParam("fullName") String fullName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam(value = "role", defaultValue = "STUDENT") String roleStr,
            Model model) {
        // kiểm tra xác nhận mật khẩu
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không trùng khớp!");
            preserveFormData(model, fullName, username, email, phone);
            return "register";
        }
        // kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có độ dài tối thiểu là 6 ký tự trở lên!");
            preserveFormData(model, fullName, username, email, phone);
            return "register";
        }
        // xác định vai trò tài khoản đăng ký
        Role userRole = Role.STUDENT;
        if ("COMPANY_ADMIN".equalsIgnoreCase(roleStr) || "employer".equalsIgnoreCase(roleStr)) {
            userRole = Role.COMPANY_ADMIN;
        }
        // khởi tạo đối tượng user mới
        User newUser = User.builder()
                .fullName(fullName.trim())
                .username(username.trim())
                .email(email.trim().toLowerCase())
                .phone(phone != null ? phone.trim() : null)
                .password(password)
                .role(userRole)
                .active(true)
                .build();
        // lưu vào csdl thông qua UserService
        try {
            userService.registerUser(newUser);
            log.info("Đăng ký thàng công tài khoản mới: {}", newUser.getUsername());
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException ex) {
            // ném ngoại lệ khi trùng username hoặc email
            model.addAttribute("error", ex.getMessage());
            preserveFormData(model, fullName, username, email, phone);
            return "register";
        } catch (Exception ex) {
            log.error("Lỗi khi đăng ký tài khoản: ", ex);
            model.addAttribute("error", "Đã xảy ra lỗi trong quá trình tạo tài khoản. Vui lòng thử lại!");
            preserveFormData(model, fullName, username, email, phone);
            return "register";
        }
    }

    // đăng xuất
    @GetMapping("/logout")
    public String processLogout(HttpSession session) {
        if (session != null) {
            session.invalidate();// hủy toàn bộ session
        }
        return "redirect:/login?logout=true";
    }

    // giữ lại dữ liệu khi form đăng nhập bị lỗi=
    private void preserveFormData(Model model, String fullName, String username, String email, String phone) {
        model.addAttribute("fullName", fullName);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
    }
}