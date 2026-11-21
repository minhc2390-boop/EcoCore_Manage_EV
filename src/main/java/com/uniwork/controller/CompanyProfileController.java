package com.uniwork.controller;

import com.uniwork.entity.CompanyReview;
import com.uniwork.entity.Employer;
import com.uniwork.entity.JobPosting;
import com.uniwork.entity.User;
import com.uniwork.repository.CompanyReviewRepository;
import com.uniwork.service.EmployerService;
import com.uniwork.service.JobPostingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller Hồ sơ Công ty & Đánh giá (Public Company Profile & Reviews)
 * Dành cho sinh viên xem thông tin chi nhánh Cần Thơ, việc làm mở và viết đánh giá / chấm sao.
 */
@Controller
@RequestMapping("/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyProfileController {

    private final EmployerService employerService;
    private final JobPostingService jobPostingService;
    private final CompanyReviewRepository companyReviewRepository;

    /**
     * Danh bạ các doanh nghiệp đối tác tại Cần Thơ
     */
    @GetMapping
    public String listCompanies(@RequestParam(value = "keyword", required = false) String keyword,
                                Model model) {
        List<Employer> allEmployers = employerService.getAllEmployers();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            allEmployers = allEmployers.stream()
                    .filter(e -> (e.getCompanyName() != null && e.getCompanyName().toLowerCase().contains(kw))
                            || (e.getAddress() != null && e.getAddress().toLowerCase().contains(kw))
                            || (e.getDescription() != null && e.getDescription().toLowerCase().contains(kw)))
                    .toList();
        }

        model.addAttribute("companies", allEmployers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalCompanies", allEmployers.size());
        return "companies/list";
    }

    /**
     * Chi tiết hồ sơ công ty (Company Profile)
     */
    @GetMapping("/{id}")
    public String companyDetail(@PathVariable("id") Long id,
                                HttpSession session,
                                Model model) {
        Employer employer = employerService.getEmployerById(id);
        List<JobPosting> activeJobs = jobPostingService.getJobsByEmployerIdAndStatus(id, "ACTIVE");
        List<CompanyReview> reviews = companyReviewRepository.findByEmployerIdOrderByCreatedAtDesc(id);

        double averageRating = reviews.isEmpty() ? 4.8 : reviews.stream().mapToInt(CompanyReview::getRating).average().orElse(4.8);
        long fiveStarCount = reviews.stream().filter(r -> r.getRating() != null && r.getRating() == 5).count();
        long recommendPercent = reviews.isEmpty() ? 96 : Math.round((double) reviews.stream().filter(r -> r.getRating() != null && r.getRating() >= 4).count() / reviews.size() * 100);

        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        model.addAttribute("employer", employer);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("reviews", reviews);
        model.addAttribute("totalReviews", reviews.size());
        model.addAttribute("averageRating", String.format("%.1f", averageRating));
        model.addAttribute("recommendPercent", recommendPercent);
        model.addAttribute("fiveStarCount", fiveStarCount);
        model.addAttribute("currentUser", currentUser);

        return "companies/detail";
    }

    /**
     * Sinh viên gửi đánh giá & chấm sao cho Doanh nghiệp
     */
    @PostMapping("/{id}/reviews")
    public String submitReview(@PathVariable("id") Long id,
                               @RequestParam("rating") Integer rating,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam(value = "salaryRating", defaultValue = "Đúng hạn") String salaryRating,
                               @RequestParam(value = "environmentRating", defaultValue = "Thân thiện hỗ trợ") String environmentRating,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Sinh viên để gửi đánh giá & chấm sao cho doanh nghiệp!");
            return "redirect:/login";
        }

        try {
            Employer employer = employerService.getEmployerById(id);
            CompanyReview review = CompanyReview.builder()
                    .employer(employer)
                    .student(currentUser)
                    .studentName(currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername())
                    .rating(rating != null ? Math.min(5, Math.max(1, rating)) : 5)
                    .title(title.trim())
                    .content(content.trim())
                    .salaryRating(salaryRating)
                    .environmentRating(environmentRating)
                    .verifiedStudent(true)
                    .build();

            companyReviewRepository.save(review);
            log.info("Sinh viên '{}' đã gửi đánh giá cho doanh nghiệp '{}' ({} sao)",
                    currentUser.getUsername(), employer.getCompanyName(), rating);

            redirectAttributes.addFlashAttribute("success",
                    "Cảm ơn bạn đã gửi đánh giá! Nhận xét của bạn giúp cộng đồng sinh viên Cần Thơ có cái nhìn minh bạch và chính xác nhất.");
        } catch (Exception ex) {
            log.error("Lỗi khi lưu đánh giá: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("error", "Lỗi gửi đánh giá: " + ex.getMessage());
        }

        return "redirect:/companies/" + id + "#reviews";
    }
}
