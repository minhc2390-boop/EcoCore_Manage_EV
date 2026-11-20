package com.uniwork.controller;

import com.uniwork.dto.JobApplicationDTO;
import com.uniwork.dto.NotificationDTO;
import com.uniwork.entity.JobPosting;
import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.service.JobApplicationService;
import com.uniwork.service.JobPostingService;
import com.uniwork.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Controller Luồng Sinh Viên Nộp Đơn Ứng Tuyển
 * Tích hợp gửi thông báo thời gian thực (SSE) đến Nhà tuyển dụng
 * @author VY - Company Admin Portal (Phần Tuyển dụng & Quản lý ứng viên)
 * @author Cao Trần Duy Minh - PC11166 (Tích hợp Real-time Notifications)
 */
@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final JobApplicationService jobApplicationService;
    private final JobPostingService jobPostingService;
    private final NotificationService notificationService;

    /**
     * Sinh viên nộp đơn ứng tuyển vào bài đăng
     * POST /jobs/{jobId}/apply
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

            // 5. Gửi thông báo thời gian thực đến Nhà tuyển dụng sở hữu tin
            try {
                JobPosting job = jobPostingService.getJobById(jobId);
                if (job != null && job.getEmployer() != null && job.getEmployer().getUser() != null) {
                    String employerUsername = job.getEmployer().getUser().getUsername();
                    String studentName = currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername();
                    NotificationDTO notif = NotificationDTO.builder()
                            .id(UUID.randomUUID().toString())
                            .title("Có hồ sơ ứng tuyển mới!")
                            .message("Sinh viên " + studentName + " vừa nộp đơn ứng tuyển cho bài: " + job.getTitle())
                            .type("success")
                            .targetUrl("/employer/applications")
                            .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")))
                            .unread(true)
                            .build();
                    notificationService.sendToUser(employerUsername, notif);
                }
            } catch (Exception ex) {
                log.warn("Không thể gửi thông báo Realtime cho Nhà tuyển dụng: {}", ex.getMessage());
            }

            redirectAttributes.addFlashAttribute("success",
                    "Đã nộp hồ sơ ứng tuyển thành công! Nhà tuyển dụng sẽ liên hệ với bạn sớm nhất.");

        } catch (IllegalStateException e) {
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
