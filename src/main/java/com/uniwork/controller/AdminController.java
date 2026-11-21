package com.uniwork.controller;

import com.uniwork.entity.Employer;
import com.uniwork.entity.JobPosting;
import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.repository.EmployerRepository;
import com.uniwork.repository.JobApplicationRepository;
import com.uniwork.repository.JobPostingRepository;
import com.uniwork.service.EmployerService;
import com.uniwork.service.JobPostingService;
import com.uniwork.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller Phân hệ Quản Trị Tối Cao (Super Admin Portal)
 * Quản lý kiểm duyệt công ty, giám sát tin tuyển dụng, chế tài 1-click khóa công ty vi phạm,
 * và thống kê báo cáo toàn sàn TP. Cần Thơ.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final EmployerService employerService;
    private final EmployerRepository employerRepository;
    private final JobPostingService jobPostingService;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserService userService;

    private User getAuthenticatedSuperAdmin(HttpSession session) {
        if (session == null) return null;
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != Role.SUPER_ADMIN) {
            return null;
        }
        return user;
    }

    /**
     * Dashboard tổng quan Super Admin
     */
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Super Admin (Quản trị viên tối cao) để truy cập!");
            return "redirect:/login";
        }

        long totalCompanies = employerService.countTotalEmployers();
        long pendingCompanies = employerService.countEmployersByStatus("PENDING");
        long approvedCompanies = employerService.countEmployersByStatus("APPROVED");
        long lockedCompanies = employerService.countEmployersByStatus("LOCKED");
        long totalJobs = jobPostingRepository.count();
        long totalApplications = jobApplicationRepository.count();

        model.addAttribute("superAdmin", superAdmin);
        model.addAttribute("totalCompanies", totalCompanies);
        model.addAttribute("pendingCompanies", pendingCompanies);
        model.addAttribute("approvedCompanies", approvedCompanies);
        model.addAttribute("lockedCompanies", lockedCompanies);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("employedRate", "86.4%");

        List<Employer> recentEmployers = employerService.getAllEmployers().stream().limit(6).toList();
        List<JobPosting> latestJobs = jobPostingService.getLatestJobs();

        model.addAttribute("recentEmployers", recentEmployers);
        model.addAttribute("latestJobs", latestJobs);

        return "admin/dashboard";
    }

    /**
     * Danh sách kiểm duyệt và quản lý doanh nghiệp
     */
    @GetMapping("/companies")
    public String listCompanies(@RequestParam(value = "status", defaultValue = "ALL") String status,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) {
            return "redirect:/login";
        }

        List<Employer> companies = employerService.getEmployersByStatus(status);
        long pendingCount = employerService.countEmployersByStatus("PENDING");
        long approvedCount = employerService.countEmployersByStatus("APPROVED");
        long lockedCount = employerService.countEmployersByStatus("LOCKED");
        long totalCount = employerService.countTotalEmployers();

        model.addAttribute("superAdmin", superAdmin);
        model.addAttribute("companies", companies);
        model.addAttribute("currentStatus", status.toUpperCase());
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("lockedCount", lockedCount);
        model.addAttribute("totalCount", totalCount);

        return "admin/companies/list";
    }

    /**
     * Phê duyệt hồ sơ công ty
     */
    @PostMapping("/companies/{id}/approve")
    public String approveCompany(@PathVariable("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) return "redirect:/login";

        try {
            Employer approved = employerService.approveEmployer(id);
            redirectAttributes.addFlashAttribute("success",
                    "Đã PHÊ DUYỆT thành công hồ sơ của '" + approved.getCompanyName() + "'. Doanh nghiệp hiện có thể đăng tin tuyển dụng sinh viên!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi phê duyệt: " + ex.getMessage());
        }
        return "redirect:/admin/companies";
    }

    /**
     * Chế tài: 1-click Khóa công ty vi phạm chính sách / lừa đảo sinh viên Cần Thơ
     */
    @PostMapping("/companies/{id}/lock")
    public String lockCompany(@PathVariable("id") Long id,
                              @RequestParam(value = "reason", required = false) String reason,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) return "redirect:/login";

        try {
            Employer locked = employerService.lockEmployer(id);
            redirectAttributes.addFlashAttribute("warning",
                    "Đã kích hoạt chế tài KHÓA 1-CLICK đối với '" + locked.getCompanyName() + "'. Mọi bài đăng của đơn vị này đã bị tạm ngừng hiển thị!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi khóa: " + ex.getMessage());
        }
        return "redirect:/admin/companies";
    }

    /**
     * Mở khóa doanh nghiệp
     */
    @PostMapping("/companies/{id}/unlock")
    public String unlockCompany(@PathVariable("id") Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) return "redirect:/login";

        try {
            Employer unlocked = employerService.unlockEmployer(id);
            redirectAttributes.addFlashAttribute("success",
                    "Đã MỞ KHÓA thành công cho doanh nghiệp: " + unlocked.getCompanyName());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi mở khóa: " + ex.getMessage());
        }
        return "redirect:/admin/companies";
    }

    /**
     * Giám sát bài đăng tuyển dụng toàn TP. Cần Thơ
     */
    @GetMapping("/jobs")
    public String monitorJobs(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) return "redirect:/login";

        List<JobPosting> allJobs = jobPostingRepository.findAll();
        model.addAttribute("superAdmin", superAdmin);
        model.addAttribute("jobs", allJobs);
        model.addAttribute("totalJobs", allJobs.size());

        return "admin/jobs/list";
    }

    /**
     * Báo cáo tổng sàn Cần Thơ (Thống kê toàn diện)
     */
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) return "redirect:/login";

        model.addAttribute("superAdmin", superAdmin);
        model.addAttribute("totalCompanies", employerService.countTotalEmployers());
        model.addAttribute("totalJobs", jobPostingRepository.count());
        model.addAttribute("totalApplications", jobApplicationRepository.count());
        model.addAttribute("employedRate", "86.4%");

        return "admin/reports";
    }

    /**
     * Quản lý danh sách tài khoản doanh nghiệp (COMPANY_ADMIN)
     */
    @GetMapping("/users/companies")
    public String listCompanyUsers(@RequestParam(value = "status", defaultValue = "ALL") String status,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) {
            return "redirect:/login";
        }

        List<User> companyUsers = userService.searchUsers(Role.COMPANY_ADMIN, status, keyword);
        for (User u : companyUsers) {
            if (u.getCompany() == null) {
                employerRepository.findByUserId(u.getId()).ifPresent(u::setCompany);
            }
        }

        long totalCount = userService.countUsersByRole(Role.COMPANY_ADMIN);
        long activeCount = userService.countUsersByRoleAndStatus(Role.COMPANY_ADMIN, true);
        long lockedCount = userService.countUsersByRoleAndStatus(Role.COMPANY_ADMIN, false);

        model.addAttribute("superAdmin", superAdmin);
        model.addAttribute("users", companyUsers);
        model.addAttribute("currentStatus", status.toUpperCase());
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("lockedCount", lockedCount);

        return "admin/users/companies";
    }

    /**
     * Quản lý danh sách tài khoản khách hàng / sinh viên (STUDENT)
     */
    @GetMapping("/users/students")
    public String listStudentUsers(@RequestParam(value = "status", defaultValue = "ALL") String status,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) {
            return "redirect:/login";
        }

        List<User> studentUsers = userService.searchUsers(Role.STUDENT, status, keyword);
        long totalCount = userService.countUsersByRole(Role.STUDENT);
        long activeCount = userService.countUsersByRoleAndStatus(Role.STUDENT, true);
        long lockedCount = userService.countUsersByRoleAndStatus(Role.STUDENT, false);

        model.addAttribute("superAdmin", superAdmin);
        model.addAttribute("users", studentUsers);
        model.addAttribute("currentStatus", status.toUpperCase());
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("lockedCount", lockedCount);

        return "admin/users/students";
    }

    /**
     * Khóa hoặc Mở khóa tài khoản người dùng
     */
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Long id,
                                   @RequestParam(value = "redirectType", defaultValue = "companies") String redirectType,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) return "redirect:/login";

        try {
            User updated = userService.toggleUserStatus(id);
            if (Boolean.TRUE.equals(updated.getActive())) {
                redirectAttributes.addFlashAttribute("success",
                        "Đã MỞ KHÓA thành công tài khoản: '" + updated.getUsername() + "' (" + updated.getFullName() + ")");
            } else {
                redirectAttributes.addFlashAttribute("warning",
                        "Đã KHÓA tài khoản: '" + updated.getUsername() + "'. Người dùng này sẽ không thể đăng nhập vào hệ thống!");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý tài khoản: " + ex.getMessage());
        }

        return "redirect:/admin/users/" + redirectType;
    }

    /**
     * Đặt lại mật khẩu tài khoản về mặc định 123456
     */
    @PostMapping("/users/{id}/reset-password")
    public String resetUserPassword(@PathVariable("id") Long id,
                                    @RequestParam(value = "redirectType", defaultValue = "companies") String redirectType,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User superAdmin = getAuthenticatedSuperAdmin(session);
        if (superAdmin == null) return "redirect:/login";

        try {
            User updated = userService.resetUserPassword(id, "123456");
            redirectAttributes.addFlashAttribute("success",
                    "Đã đặt lại mật khẩu cho tài khoản '" + updated.getUsername() + "' về mặc định '123456' thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi đặt lại mật khẩu: " + ex.getMessage());
        }

        return "redirect:/admin/users/" + redirectType;
    }
}
