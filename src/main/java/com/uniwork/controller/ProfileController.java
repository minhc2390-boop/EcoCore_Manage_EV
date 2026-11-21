package com.uniwork.controller;

import com.uniwork.entity.JobApplication;
import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.repository.UserRepository;
import com.uniwork.service.JobApplicationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Controller Quản lý hồ sơ cá nhân và đổi mật khẩu
 * @author Cao Trần Duy Minh - PC11166
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final UserRepository userRepository;
    private final JobApplicationService jobApplicationService;

    // Hiển thị trang cá nhân của người dùng đăng nhập
    @GetMapping
    public String showProfile(HttpSession ss, Model model) {
        // lấy thông tin user từ session
        User sessionUser = (User) ss.getAttribute("currentUser");
        // Nếu chưa đăng nhập thì về login
        if (sessionUser == null || sessionUser.getId() == null) {
            return "redirect:/login";
        }
        // Lấy dữ liệu mới nhất từ csdl ra để hiển thị (đảm bảo null-safety cho @NonNull Long id)
        Long userId = Objects.requireNonNull(sessionUser.getId());
        User currentUser = userRepository.findById(userId).orElse(sessionUser);
        model.addAttribute("user", currentUser);

        // Lấy danh sách lịch sử nộp hồ sơ ứng tuyển nếu người dùng là Sinh viên
        List<JobApplication> myApplications = Collections.emptyList();
        if (currentUser.getRole() == Role.STUDENT && currentUser.getId() != null) {
            try {
                myApplications = jobApplicationService.getApplicationsByStudent(currentUser.getId());
            } catch (Exception ex) {
                log.warn("Không thể tải danh sách ứng tuyển của sinh viên {}: {}", currentUser.getUsername(), ex.getMessage());
            }
        }
        model.addAttribute("myApplications", myApplications);

        return "profile";
    }

    // xử lý cập nhật thông tin cá nhân
    @PostMapping("/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
            @RequestParam(value = "phone", required = false) String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("currentUser");
        if (sessionUser == null || sessionUser.getId() == null) {
            return "redirect:/login";
        }

        try {
            Long userId = Objects.requireNonNull(sessionUser.getId());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin người dùng!"));

            // cập nhật thông tin mới
            user.setFullName(fullName.trim());
            user.setPhone(phone != null ? phone.trim() : null);
            // lưu vào data base
            userRepository.save(user);
            // cập nhật lại thông tin mới vào session
            session.setAttribute("currentUser", user);

            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin cá nhân thành công!");
            log.info("Người dùng '{}' vừa cập nhật thông tin cá nhân.", user.getUsername());
        } catch (Exception ex) {
            log.error("Lỗi khi cập nhật hồ sơ", ex);
            redirectAttributes.addFlashAttribute("error", "Cập nhật thất bại. Vui lòng thử lại!");
        }
        return "redirect:/profile";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("currentUser");
        if (sessionUser == null || sessionUser.getId() == null) {
            return "redirect:/login";
        }

        Long userId = Objects.requireNonNull(sessionUser.getId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản!"));

        // Kiểm tra mật khẩu hiện tại có đúng không
        if (!user.getPassword().equalsIgnoreCase(currentPassword)) {
            redirectAttributes.addFlashAttribute("pwdError", "Mật khẩu hiện tại không chính xác!");
            return "redirect:/profile";
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!newPassword.equalsIgnoreCase(confirmPassword)) {
            redirectAttributes.addFlashAttribute("pwdError", "Mật khẩu mới và xác nhận mật khẩu không trùng khớp!");
            return "redirect:/profile";
        }

        // Kiểm tra độ dài mật khẩu mới
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("pwdError", "Mật khẩu mới phải dài hơn 6 kí tự");
            return "redirect:/profile";
        }

        // lưu mật khẩu vào cơ sở dữ liệu
        user.setPassword(newPassword);
        userRepository.save(user);

        // cập nhật lại session
        session.setAttribute("currentUser", user);
        redirectAttributes.addFlashAttribute("pwdSuccess", "Đổi mật khẩu thành công!");
        log.info("Người dùng '{}' đã đổi mật khẩu thành công", user.getUsername());

        return "redirect:/profile";
    }
}
