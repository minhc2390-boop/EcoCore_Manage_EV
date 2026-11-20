package com.uniwork.controller;

import com.uniwork.dto.JobSearchDTO;
import com.uniwork.entity.JobPosting;
import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.service.JobApplicationService;
import com.uniwork.service.JobCategoryService;
import com.uniwork.service.JobPostingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobPostingService jobPostingService;
    private final JobCategoryService jobCategoryService;
    private final JobApplicationService jobApplicationService;

    @GetMapping
    public String listJobs(@ModelAttribute JobSearchDTO searchDTO,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           Model model) {
        if (page < 0) {
            page = 0;
        }
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<JobPosting> jobPage = jobPostingService.searchJobsPaged(searchDTO, pageable);

        model.addAttribute("jobPage", jobPage);
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", jobPage.getNumber());
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalElements", jobPage.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("categories", jobCategoryService.getAllActiveCategories());
        model.addAttribute("searchDTO", searchDTO);
        return "jobs/list";
    }

    /**
     * Trang chi tiết việc làm — VY thêm logic kiểm tra sinh viên đã nộp chưa
     * để hiển thị đúng trạng thái nút ứng tuyển trên giao diện.
     */
    @GetMapping("/{id}")
    public String jobDetail(@PathVariable Long id, HttpSession session, Model model) {
        jobPostingService.incrementViewCount(id);
        JobPosting job = jobPostingService.getJobById(id);
        model.addAttribute("job", job);

        // Lấy thông tin người dùng hiện tại từ session (VY — phục vụ form ứng tuyển)
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);

        // Kiểm tra sinh viên đã nộp đơn ứng tuyển cho bài đăng này chưa
        boolean hasApplied = false;
        if (currentUser != null && currentUser.getRole() == Role.STUDENT) {
            try {
                hasApplied = jobApplicationService.hasApplied(currentUser.getId(), id);
            } catch (Exception e) {
                // An toàn: nếu có lỗi thì mặc định chưa nộp
                hasApplied = false;
            }
        }
        model.addAttribute("hasApplied", hasApplied);

        return "jobs/detail";
    }
}
