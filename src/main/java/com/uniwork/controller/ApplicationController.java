package com.uniwork.controller;

import com.uniwork.dto.JobApplicationDTO;
import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.service.JobApplicationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller Luồng Sinh Viên Nộp Đơn Ứng Tuyển
 * Phụ trách: Sinh viên bấm "Nộp Hồ Sơ Ngay" tại trang chi tiết việc làm,
 * form submit thật sự vào DB (không phải alert fake nữa).
 *
 * @author VY - Company Admin Portal (Phần Tuyển dụng & Quản lý ứng viên)
 */
@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final JobApplicationService jobApplicationService;

    /**
     * Sinh viên nộp đơn ứng tuyển vào bài đăng
     * POST /jobs/{jobId}/apply
     *
     * - Kiểm tra đăng nhập (session-based, không có Spring Security)
     * - Kiểm tra role STUDENT
     * - Kiểm tra chưa nộp trước đó
     * - Lưu đơn vào DB với trạng thái PENDING
     */
    @PostMapping("/{jobId}/apply")
    public String applyForJob(@PathVariable("jobId") Long jobId,
                              @RequestParam(value = "coverLetter", required = false) String coverLetter,
                              @RequestParam(value = "studentScheduleNotes", required = false) String studentScheduleNotes,
                              @RequestParam(value = "cvUrl", required = false) String cvUrl,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        // 1. Kiểm tra đăng nhập
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Vui lòng đăng nhập để nộp hồ sơ ứng tuyển!");
            return "redirect:/login";
        }

        // 2. Kiểm tra role phải là STUDENT
        if (currentUser.getRole() != Role.STUDENT) {
            redirectAttributes.addFlashAttribute("error",
                    "Chỉ tài khoản sinh viên mới có thể nộp hồ sơ ứng tuyển!");
            return "redirect:/jobs/" + jobId;
        }

        // 3. Kiểm tra đã nộp chưa
        if (jobApplicationService.hasApplied(currentUser.getId(), jobId)) {
            redirectAttributes.addFlashAttribute("error",
                    "Bạn đã nộp hồ sơ ứng tuyển cho công việc này rồi! Vui lòng chờ phản hồi từ nhà tuyển dụng.");
            return "redirect:/jobs/" + jobId;
        }

        // 4. Tạo DTO và lưu đơn ứng tuyển
        try {
            JobApplicationDTO dto = new JobApplicationDTO();
            dto.setJobPostingId(jobId);
            dto.setStudentId(currentUser.getId());
            dto.setCoverLetter(coverLetter != null ? coverLetter.trim() : "");
            dto.setStudentScheduleNotes(studentScheduleNotes != null ? studentScheduleNotes.trim() : "");
            dto.setCvUrl(cvUrl != null ? cvUrl.trim() : "");

            jobApplicationService.applyForJob(dto);

            log.info("Sinh viên '{}' (ID: {}) đã nộp đơn ứng tuyển vào bài đăng ID: {}",
                    currentUser.getUsername(), currentUser.getId(), jobId);

            redirectAttributes.addFlashAttribute("success",
                    "Đã nộp hồ sơ ứng tuyển thành công! Nhà tuyển dụng sẽ liên hệ với bạn sớm nhất.");

        } catch (IllegalStateException e) {
            // Đã nộp rồi (double-check từ service)
            log.warn("Sinh viên '{}' cố nộp trùng đơn ứng tuyển bài ID: {}", currentUser.getUsername(), jobId);
            redirectAttributes.addFlashAttribute("error", e.getMessage());

        } catch (Exception e) {
            log.error("Lỗi khi sinh viên '{}' nộp đơn ứng tuyển bài ID: {}: {}",
                    currentUser.getUsername(), jobId, e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi nộp hồ sơ. Vui lòng thử lại!");
        }

        return "redirect:/jobs/" + jobId;
    }
}
