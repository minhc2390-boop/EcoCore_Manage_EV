package com.uniwork.controller;

import com.uniwork.dto.JobPostingDTO;
import com.uniwork.dto.NotificationDTO;
import com.uniwork.entity.Employer;
import com.uniwork.entity.JobApplication;
import com.uniwork.entity.JobCategory;
import com.uniwork.entity.JobPosting;
import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.service.EmployerService;
import com.uniwork.service.JobApplicationService;
import com.uniwork.service.JobCategoryService;
import com.uniwork.service.JobPostingService;
import com.uniwork.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Controller Cổng Quản Trị Tuyển Dụng Dành Riêng Cho Company Admin
 * Phụ trách: Giao diện đăng bài tuyển dụng theo ca, chọn quận/huyện ở Cần Thơ,
 * và quản lý danh sách các bài đăng của riêng công ty mình (Sửa, ẩn tin, gia hạn).
 * 
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
@Controller
@RequestMapping("/employer")
@RequiredArgsConstructor
@Slf4j
public class CompanyJobController {

    private final JobPostingService jobPostingService;
    private final EmployerService employerService;
    private final JobCategoryService jobCategoryService;
    private final JobApplicationService jobApplicationService; // VY — quản lý ứng viên
    private final NotificationService notificationService;

    // Danh sách 9 quận/huyện trực thuộc TP. Cần Thơ
    public static final List<String> CAN_THO_DISTRICTS = List.of(
            "Quận Ninh Kiều",
            "Quận Cái Răng",
            "Quận Bình Thủy",
            "Quận Ô Môn",
            "Quận Thốt Nốt",
            "Huyện Phong Điền",
            "Huyện Cờ Đỏ",
            "Huyện Thới Lai",
            "Huyện Vĩnh Thạnh"
    );

    // Danh sách các ca làm việc mẫu dành cho sinh viên Cần Thơ
    public static final List<String> POPULAR_SHIFTS = List.of(
            "Ca sáng (07:00 - 11:30)",
            "Ca trưa (11:30 - 15:30)",
            "Ca chiều (13:30 - 17:30)",
            "Ca tối (17:30 - 22:30)",
            "Ca đêm (22:00 - 06:00)",
            "Ca xoay linh hoạt (Đăng ký theo lịch học sinh viên)",
            "Cuối tuần (Thứ 7 & Chủ Nhật)"
    );

    // Danh sách hình thức làm việc
    public static final List<String> JOB_TYPES = List.of(
            "Part-time (Bán thời gian)",
            "Thực tập sinh (Internship)",
            "Freelance / Làm từ xa",
            "Ca xoay linh hoạt",
            "Full-time"
    );

    @GetMapping({"", "/"})
    public String redirectToDashboard() {
        return "redirect:/employer/dashboard";
    }

    @GetMapping("/dashboard")
    public String showEmployerDashboard(HttpSession session,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Nhà tuyển dụng (Company Admin) để truy cập!");
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);
        List<JobPosting> allJobs = jobPostingService.getJobsByEmployerId(employer.getId());
        List<JobPosting> recentJobs = allJobs.stream().limit(5).toList();

        long totalJobs = jobPostingService.countJobsByEmployer(employer.getId());
        long activeJobs = jobPostingService.countJobsByEmployerAndStatus(employer.getId(), "ACTIVE");
        long hiddenJobs = jobPostingService.countJobsByEmployerAndStatus(employer.getId(), "HIDDEN");
        long totalViews = jobPostingService.getTotalViewsByEmployer(employer.getId());

        model.addAttribute("jobs", allJobs);
        model.addAttribute("recentJobs", recentJobs);
        model.addAttribute("employer", employer);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("hiddenJobs", hiddenJobs);
        model.addAttribute("totalViews", totalViews);

        return "employer/dashboard";
    }

    @GetMapping("/profile")
    public String showCompanyProfile(HttpSession session,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Nhà tuyển dụng để truy cập!");
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);
        long totalJobs = jobPostingService.countJobsByEmployer(employer.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("employer", employer);
        model.addAttribute("totalJobs", totalJobs);

        return "employer/profile";
    }

    @PostMapping("/profile")
    public String updateCompanyProfile(@ModelAttribute("employer") Employer formEmployer,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Employer existingEmployer = employerService.getOrCreateEmployerForUser(currentUser);
        if (formEmployer.getCompanyName() != null && !formEmployer.getCompanyName().trim().isEmpty()) {
            existingEmployer.setCompanyName(formEmployer.getCompanyName().trim());
        }
        if (formEmployer.getCompanyLogo() != null) {
            existingEmployer.setCompanyLogo(formEmployer.getCompanyLogo().trim());
        }
        if (formEmployer.getWebsite() != null) {
            existingEmployer.setWebsite(formEmployer.getWebsite().trim());
        }
        if (formEmployer.getAddress() != null) {
            existingEmployer.setAddress(formEmployer.getAddress().trim());
        }
        if (formEmployer.getDescription() != null) {
            existingEmployer.setDescription(formEmployer.getDescription().trim());
        }
        if (formEmployer.getContactEmail() != null) {
            existingEmployer.setContactEmail(formEmployer.getContactEmail().trim());
        }
        if (formEmployer.getContactPhone() != null) {
            existingEmployer.setContactPhone(formEmployer.getContactPhone().trim());
        }

        try {
            employerService.saveEmployer(existingEmployer);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin hồ sơ doanh nghiệp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật hồ sơ doanh nghiệp: " + e.getMessage());
        }

        return "redirect:/employer/profile";
    }

    /**
     * 1. DANH SÁCH BÀI ĐĂNG TUYỂN DỤNG CỦA RIÊNG CÔNG TY MÌNH
     * Có thống kê số lượng tin, lọc theo trạng thái, tìm kiếm nội bộ công ty.
     */
    @GetMapping("/jobs")
    public String listCompanyJobs(@RequestParam(value = "status", required = false) String status,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Nhà tuyển dụng (Company Admin) để truy cập!");
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        // Lấy danh sách tin tuyển dụng của riêng công ty này
        List<JobPosting> jobs;
        if (keyword != null && !keyword.trim().isEmpty()) {
            jobs = jobPostingService.searchJobsByEmployer(employer.getId(), keyword.trim());
        } else if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            jobs = jobPostingService.getJobsByEmployerIdAndStatus(employer.getId(), status.trim());
        } else {
            jobs = jobPostingService.getJobsByEmployerId(employer.getId());
        }

        // Tính các chỉ số thống kê của riêng công ty
        long totalJobs = jobPostingService.countJobsByEmployer(employer.getId());
        long activeJobs = jobPostingService.countJobsByEmployerAndStatus(employer.getId(), "ACTIVE");
        long hiddenJobs = jobPostingService.countJobsByEmployerAndStatus(employer.getId(), "HIDDEN");
        long totalViews = jobPostingService.getTotalViewsByEmployer(employer.getId());

        model.addAttribute("jobs", jobs);
        model.addAttribute("employer", employer);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("hiddenJobs", hiddenJobs);
        model.addAttribute("totalViews", totalViews);
        model.addAttribute("currentStatus", status != null ? status : "ALL");
        model.addAttribute("keyword", keyword);

        return "employer/jobs/list";
    }

    /**
     * 2. GIAO DIỆN ĐĂNG BÀI TUYỂN DỤNG MỚI THEO CA VÀ QUẬN/HUYỆN CẦN THƠ
     */
    @GetMapping("/jobs/create")
    public String showCreateJobForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Company Admin!");
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        // Chuẩn bị DTO với dữ liệu mặc định gợi ý
        JobPostingDTO jobDTO = JobPostingDTO.builder()
                .district("Quận Ninh Kiều")
                .jobType("Part-time (Bán thời gian)")
                .shiftSchedule("Ca xoay linh hoạt (Đăng ký theo lịch học sinh viên)")
                .salaryRange("23.000đ - 28.000đ / giờ")
                .deadline(LocalDate.now().plusDays(30))
                .status("ACTIVE")
                .build();

        populateFormMetadata(model);
        model.addAttribute("jobDTO", jobDTO);
        model.addAttribute("employer", employer);

        return "employer/jobs/create";
    }

    /**
     * XỬ LÝ LƯU BÀI ĐĂNG TUYỂN DỤNG MỚI
     */
    @PostMapping("/jobs/create")
    public String processCreateJob(@ModelAttribute("jobDTO") JobPostingDTO jobDTO,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        // Ràng buộc kiểm tra dữ liệu bắt buộc
        if (jobDTO.getTitle() == null || jobDTO.getTitle().trim().isEmpty()) {
            model.addAttribute("error", "Tiêu đề tin tuyển dụng không được để trống!");
            populateFormMetadata(model);
            model.addAttribute("employer", employer);
            return "employer/jobs/create";
        }

        // Tạo chuỗi địa chỉ gắn liền với quận/huyện ở Cần Thơ
        String location = buildLocation(jobDTO.getDetailAddress(), jobDTO.getDistrict());

        // Khởi tạo thực thể JobPosting gắn với Employer của Company Admin
        JobPosting newJob = JobPosting.builder()
                .title(jobDTO.getTitle().trim())
                .description(jobDTO.getDescription() != null ? jobDTO.getDescription().trim() : "")
                .requirements(jobDTO.getRequirements() != null ? jobDTO.getRequirements().trim() : "")
                .benefits(jobDTO.getBenefits() != null ? jobDTO.getBenefits().trim() : "")
                .salaryRange(jobDTO.getSalaryRange() != null && !jobDTO.getSalaryRange().trim().isEmpty()
                        ? jobDTO.getSalaryRange().trim()
                        : "Thỏa thuận")
                .location(location)
                .jobType(jobDTO.getJobType() != null ? jobDTO.getJobType() : "Part-time (Bán thời gian)")
                .shiftSchedule(jobDTO.getShiftSchedule() != null && !jobDTO.getShiftSchedule().trim().isEmpty()
                        ? jobDTO.getShiftSchedule().trim()
                        : "Ca linh hoạt theo lịch học")
                .deadline(jobDTO.getDeadline() != null ? jobDTO.getDeadline() : LocalDate.now().plusDays(30))
                .status(jobDTO.getStatus() != null ? jobDTO.getStatus() : "ACTIVE")
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .employer(employer)
                .build();

        // Gán danh mục nghề nghiệp (nếu có, phụ thuộc vào module JobCategory của Khanh)
        if (jobDTO.getCategoryId() != null) {
            try {
                JobCategory category = jobCategoryService.getCategoryById(jobDTO.getCategoryId());
                newJob.setCategory(category);
            } catch (Exception e) {
                log.warn("Không thể tìm thấy danh mục id = {}: {}", jobDTO.getCategoryId(), e.getMessage());
            }
        }

        jobPostingService.saveJob(newJob);
        log.info("Company Admin '{}' (Công ty: {}) đã đăng tin tuyển dụng mới ID: {}",
                currentUser.getUsername(), employer.getCompanyName(), newJob.getId());

        // Phát thông báo thời gian thực về việc làm mới tới toàn hệ thống
        try {
            NotificationDTO broadcastNotif = NotificationDTO.builder()
                    .id(UUID.randomUUID().toString())
                    .title("Việc làm mới tại Cần Thơ!")
                    .message("'" + newJob.getTitle() + "' vừa được " + employer.getCompanyName() + " đăng tuyển dụng.")
                    .type("primary")
                    .targetUrl("/jobs/" + newJob.getId())
                    .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")))
                    .unread(true)
                    .build();
            notificationService.broadcast(broadcastNotif);
        } catch (Exception ex) {
            log.warn("Lỗi phát sóng thông báo việc làm mới: {}", ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("success",
                "Đăng bài tuyển dụng '" + newJob.getTitle() + "' thành công! Tin đã hiển thị cho sinh viên Cần Thơ ứng tuyển.");
        return "redirect:/employer/jobs";
    }

    /**
     * 3. GIAO DIỆN CHỈNH SỬA BÀI ĐĂNG CỦA CÔNG TY (SỬA TIN)
     */
    @GetMapping("/jobs/edit/{id}")
    public String showEditJobForm(@PathVariable("id") Long id,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        // Kiểm tra bảo mật: Bài đăng phải thuộc về công ty này
        JobPosting job;
        try {
            job = jobPostingService.getJobByIdAndEmployerId(id, employer.getId());
        } catch (Exception e) {
            log.warn("Cảnh báo bảo mật: User '{}' cố gắng truy cập bài đăng ID: {} không thuộc công ty mình!",
                    currentUser.getUsername(), id);
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền chỉnh sửa tin tuyển dụng của công ty khác!");
            return "redirect:/employer/jobs";
        }

        // Tách quận/huyện và địa chỉ chi tiết từ chuỗi location
        String extractedDistrict = extractDistrict(job.getLocation());
        String extractedDetail = extractDetailAddress(job.getLocation(), extractedDistrict);

        JobPostingDTO jobDTO = JobPostingDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .benefits(job.getBenefits())
                .salaryRange(job.getSalaryRange())
                .location(job.getLocation())
                .district(extractedDistrict)
                .detailAddress(extractedDetail)
                .jobType(job.getJobType())
                .shiftSchedule(job.getShiftSchedule())
                .deadline(job.getDeadline())
                .categoryId(job.getCategory() != null ? job.getCategory().getId() : null)
                .employerId(employer.getId())
                .status(job.getStatus())
                .build();

        populateFormMetadata(model);
        model.addAttribute("jobDTO", jobDTO);
        model.addAttribute("job", job);
        model.addAttribute("employer", employer);

        return "employer/jobs/edit";
    }

    /**
     * XỬ LÝ CẬP NHẬT BÀI ĐĂNG TUYỂN DỤNG (SỬA TIN)
     */
    @PostMapping("/jobs/edit/{id}")
    public String processEditJob(@PathVariable("id") Long id,
                                 @ModelAttribute("jobDTO") JobPostingDTO jobDTO,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        // Kiểm tra quyền sở hữu bài đăng của công ty
        JobPosting existingJob;
        try {
            existingJob = jobPostingService.getJobByIdAndEmployerId(id, employer.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Bài đăng không tồn tại hoặc bạn không có quyền cập nhật!");
            return "redirect:/employer/jobs";
        }

        // Ràng buộc kiểm tra
        if (jobDTO.getTitle() == null || jobDTO.getTitle().trim().isEmpty()) {
            model.addAttribute("error", "Tiêu đề tin tuyển dụng không được để trống!");
            populateFormMetadata(model);
            model.addAttribute("employer", employer);
            model.addAttribute("job", existingJob);
            return "employer/jobs/edit";
        }

        String location = buildLocation(jobDTO.getDetailAddress(), jobDTO.getDistrict());

        // Cập nhật thông tin
        existingJob.setTitle(jobDTO.getTitle().trim());
        existingJob.setDescription(jobDTO.getDescription() != null ? jobDTO.getDescription().trim() : "");
        existingJob.setRequirements(jobDTO.getRequirements() != null ? jobDTO.getRequirements().trim() : "");
        existingJob.setBenefits(jobDTO.getBenefits() != null ? jobDTO.getBenefits().trim() : "");
        existingJob.setSalaryRange(jobDTO.getSalaryRange() != null && !jobDTO.getSalaryRange().trim().isEmpty()
                ? jobDTO.getSalaryRange().trim()
                : "Thỏa thuận");
        existingJob.setLocation(location);
        existingJob.setJobType(jobDTO.getJobType() != null ? jobDTO.getJobType() : existingJob.getJobType());
        existingJob.setShiftSchedule(jobDTO.getShiftSchedule() != null && !jobDTO.getShiftSchedule().trim().isEmpty()
                ? jobDTO.getShiftSchedule().trim()
                : existingJob.getShiftSchedule());
        existingJob.setDeadline(jobDTO.getDeadline());
        if (jobDTO.getStatus() != null && !jobDTO.getStatus().trim().isEmpty()) {
            existingJob.setStatus(jobDTO.getStatus());
        }

        // Cập nhật ngành nghề nếu có
        if (jobDTO.getCategoryId() != null) {
            try {
                JobCategory category = jobCategoryService.getCategoryById(jobDTO.getCategoryId());
                existingJob.setCategory(category);
            } catch (Exception e) {
                log.warn("Không tìm thấy danh mục id={}: {}", jobDTO.getCategoryId(), e.getMessage());
            }
        }

        jobPostingService.saveJob(existingJob);
        log.info("Company Admin '{}' đã cập nhật thành công bài đăng ID: {}", currentUser.getUsername(), id);

        redirectAttributes.addFlashAttribute("success",
                "Cập nhật bài đăng tuyển dụng '" + existingJob.getTitle() + "' thành công!");
        return "redirect:/employer/jobs";
    }

    /**
     * 4. ẨN TIN / KÍCH HOẠT HIỂN THỊ TIN TUYỂN DỤNG (ẨN TIN)
     */
    @PostMapping("/jobs/{id}/toggle-status")
    public String toggleJobStatus(@PathVariable("id") Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        try {
            JobPosting updatedJob = jobPostingService.toggleJobStatus(id, employer.getId());
            if ("HIDDEN".equalsIgnoreCase(updatedJob.getStatus())) {
                redirectAttributes.addFlashAttribute("info",
                        "Đã ẩn bài đăng '" + updatedJob.getTitle() + "'. Tin này sẽ tạm thời không hiển thị cho sinh viên.");
            } else {
                redirectAttributes.addFlashAttribute("success",
                        "Đã kích hoạt hiển thị lại bài đăng '" + updatedJob.getTitle() + "'. Tin đã sẵn sàng cho ứng viên.");
            }
        } catch (Exception e) {
            log.error("Lỗi khi chuyển đổi trạng thái tin ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/employer/jobs";
    }

    /**
     * 5. GIA HẠN THỜI GIAN TUYỂN DỤNG (GIA HẠN)
     */
    @PostMapping("/jobs/{id}/extend")
    public String extendJobDeadline(@PathVariable("id") Long id,
                                    @RequestParam(value = "days", required = false) Integer days,
                                    @RequestParam(value = "newDeadline", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDeadline,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        try {
            JobPosting updatedJob;
            if (newDeadline != null) {
                updatedJob = jobPostingService.extendJobDeadline(id, employer.getId(), newDeadline);
                redirectAttributes.addFlashAttribute("success",
                        "Gia hạn bài đăng '" + updatedJob.getTitle() + "' thành công đến ngày: " + newDeadline);
            } else if (days != null && days > 0) {
                updatedJob = jobPostingService.extendJobDeadline(id, employer.getId(), days);
                redirectAttributes.addFlashAttribute("success",
                        "Gia hạn thêm " + days + " ngày cho bài đăng '" + updatedJob.getTitle() + "' thành công! Hạn mới: " + updatedJob.getDeadline());
            } else {
                // Mặc định gia hạn thêm 30 ngày nếu không chỉ định
                updatedJob = jobPostingService.extendJobDeadline(id, employer.getId(), 30);
                redirectAttributes.addFlashAttribute("success",
                        "Gia hạn thêm 30 ngày thành công! Hạn nộp mới: " + updatedJob.getDeadline());
            }
        } catch (Exception e) {
            log.error("Lỗi khi gia hạn bài đăng ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Lỗi gia hạn bài đăng: " + e.getMessage());
        }

        return "redirect:/employer/jobs";
    }

    /**
     * XÓA / ĐÓNG BÀI ĐĂNG TUYỂN DỤNG
     */
    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable("id") Long id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        try {
            jobPostingService.deleteJobByEmployer(id, employer.getId());
            redirectAttributes.addFlashAttribute("success", "Đã xóa / đóng tin tuyển dụng thành công!");
        } catch (Exception e) {
            log.error("Lỗi xóa bài đăng ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/employer/jobs";
    }

    @GetMapping("/applicants")
    public String listApplicants(HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Nhà tuyển dụng để truy cập!");
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);
        List<JobPosting> jobs = jobPostingService.getJobsByEmployerId(employer.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("employer", employer);
        model.addAttribute("jobs", jobs);

        return "employer/applicants/list";
    }

    @GetMapping("/applicants/{id}")
    public String showApplicantDetail(@PathVariable("id") Long id,
                                      HttpSession session,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Nhà tuyển dụng để truy cập!");
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("employer", employer);
        model.addAttribute("applicantId", id);

        return "employer/applicants/detail";
    }

    @GetMapping("/reviews")
    public String showCompanyReviews(HttpSession session,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Nhà tuyển dụng để truy cập!");
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("employer", employer);

        return "employer/reviews";
    }

    // =========================================================================
    // CÁC HÀM HELPER HỖ TRỢ XỬ LÝ DỮ LIỆU & BẢO MẬT
    // =========================================================================

    /**
     * Kiểm tra phiên đăng nhập và xác thực vai trò Company Admin
     */
    private User getAuthenticatedCompanyAdmin(HttpSession session) {
        if (session == null) {
            return null;
        }
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return null;
        }
        if (user.getRole() != Role.COMPANY_ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            log.warn("Người dùng '{}' vai trò {} không có quyền truy cập Cổng Doanh Nghiệp (Company Admin Portal)!",
                    user.getUsername(), user.getRole());
            return null;
        }
        return user;
    }

    /**
     * Chuẩn bị danh sách quận/huyện, ca làm việc, hình thức việc làm và danh mục ngành nghề
     */
    private void populateFormMetadata(Model model) {
        model.addAttribute("canThoDistricts", CAN_THO_DISTRICTS);
        model.addAttribute("popularShifts", POPULAR_SHIFTS);
        model.addAttribute("jobTypes", JOB_TYPES);

        // Danh mục nghề nghiệp của Khanh (an toàn nếu Khanh chưa làm xong)
        List<JobCategory> categories = Collections.emptyList();
        try {
            categories = jobCategoryService.getAllActiveCategories();
        } catch (Exception e) {
            log.warn("Chưa tải được danh mục nghề nghiệp của Khanh: {}", e.getMessage());
        }
        model.addAttribute("categories", categories);
    }

    /**
     * Ghép nối chi tiết địa chỉ với quận/huyện TP. Cần Thơ
     */
    private String buildLocation(String detailAddress, String district) {
        String dist = (district != null && !district.trim().isEmpty()) ? district.trim() : "Quận Ninh Kiều";
        if (detailAddress != null && !detailAddress.trim().isEmpty()) {
            String detail = detailAddress.trim();
            if (detail.toLowerCase().contains("cần thơ")) {
                return detail;
            }
            return detail + ", " + dist + ", TP. Cần Thơ";
        }
        return dist + ", TP. Cần Thơ";
    }

    /**
     * Trích xuất quận/huyện từ chuỗi location đã lưu
     */
    private String extractDistrict(String location) {
        if (location == null) return "Quận Ninh Kiều";
        for (String district : CAN_THO_DISTRICTS) {
            if (location.contains(district)) {
                return district;
            }
        }
        return "Quận Ninh Kiều";
    }

    /**
     * Trích xuất phần địa chỉ chi tiết trước quận/huyện
     */
    private String extractDetailAddress(String location, String district) {
        if (location == null) return "";
        if (district != null && location.contains(district)) {
            int idx = location.indexOf(district);
            String prefix = location.substring(0, idx).trim();
            if (prefix.endsWith(",")) {
                prefix = prefix.substring(0, prefix.length() - 1).trim();
            }
            return prefix;
        }
        return location;
    }

    // =========================================================================
    // QUẢN LÝ ỨNG VIÊN — VY (Company Admin Portal — Phần Tuyển dụng & Ứng viên)
    // =========================================================================

    /**
     * 6. DANH SÁCH ĐƠN ỨNG TUYỂN VÀO CÔNG TY (VY)
     * GET /employer/applications
     * Hỗ trợ lọc theo trạng thái: ALL / PENDING / REVIEWING / INTERVIEW / ACCEPTED / REJECTED
     */
    @GetMapping("/applications")
    public String listApplications(@RequestParam(value = "status", required = false) String status,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản Nhà tuyển dụng để truy cập!");
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        // Lấy danh sách đơn ứng tuyển (lọc theo trạng thái nếu có)
        List<JobApplication> applications;
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            applications = jobApplicationService.getApplicationsByEmployerAndStatus(employer.getId(), status.trim().toUpperCase());
        } else {
            applications = jobApplicationService.getApplicationsByEmployer(employer.getId());
        }

        // Thống kê số lượng theo từng trạng thái
        long totalApplications = jobApplicationService.countApplicationsByEmployer(employer.getId());
        long pendingCount      = jobApplicationService.countApplicationsByEmployerAndStatus(employer.getId(), "PENDING");
        long reviewingCount    = jobApplicationService.countApplicationsByEmployerAndStatus(employer.getId(), "REVIEWING");
        long interviewCount    = jobApplicationService.countApplicationsByEmployerAndStatus(employer.getId(), "INTERVIEW");
        long acceptedCount     = jobApplicationService.countApplicationsByEmployerAndStatus(employer.getId(), "ACCEPTED");
        long rejectedCount     = jobApplicationService.countApplicationsByEmployerAndStatus(employer.getId(), "REJECTED");

        model.addAttribute("applications", applications);
        model.addAttribute("employer", employer);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("reviewingCount", reviewingCount);
        model.addAttribute("interviewCount", interviewCount);
        model.addAttribute("acceptedCount", acceptedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("currentStatus", status != null ? status.toUpperCase() : "ALL");

        return "employer/applications/list";
    }

    /**
     * 7. CẬP NHẬT TRẠNG THÁI ĐƠN ỨNG TUYỂN (VY)
     * POST /employer/applications/{id}/status?newStatus=INTERVIEW|ACCEPTED|REJECTED
     * Company Admin bấm nút: [Mời Phỏng Vấn], [Nhận Việc], [Từ Chối]
     */
    @PostMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable("id") Long id,
                                          @RequestParam("newStatus") String newStatus,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        User currentUser = getAuthenticatedCompanyAdmin(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Employer employer = employerService.getOrCreateEmployerForUser(currentUser);

        try {
            JobApplication updated = jobApplicationService.updateApplicationStatus(id, newStatus, employer.getId());
            String statusLabel = switch (updated.getStatus()) {
                case "INTERVIEW" -> "Mời phỏng vấn";
                case "ACCEPTED"  -> "Nhận việc";
                case "REJECTED"  -> "Từ chối";
                case "REVIEWING" -> "Đang xem xét";
                default -> updated.getStatus();
            };

            // Gửi thông báo thời gian thực đến sinh viên ứng tuyển
            if (updated.getStudent() != null) {
                try {
                    String studentUsername = updated.getStudent().getUsername();
                    NotificationDTO notif = NotificationDTO.builder()
                            .id(UUID.randomUUID().toString())
                            .title("Cập nhật đơn ứng tuyển!")
                            .message("Hồ sơ công việc '" + updated.getJobPosting().getTitle() + "' đã được chuyển sang: [" + statusLabel + "]")
                            .type("info")
                            .targetUrl("/jobs/" + updated.getJobPosting().getId())
                            .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")))
                            .unread(true)
                            .build();
                    notificationService.sendToUser(studentUsername, notif);
                } catch (Exception ex) {
                    log.warn("Không thể gửi thông báo Realtime cho sinh viên: {}", ex.getMessage());
                }
            }

            redirectAttributes.addFlashAttribute("success",
                    "Đã cập nhật trạng thái đơn ứng tuyển thành [" + statusLabel + "] thành công!");
            log.info("Company Admin '{}' (Employer ID: {}) cập nhật trạng thái đơn ID: {} → {}",
                    currentUser.getUsername(), employer.getId(), id, newStatus);
        } catch (IllegalStateException e) {
            log.warn("Lỗi bảo mật khi cập nhật đơn ID: {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi cập nhật trạng thái đơn ID: {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật trạng thái: " + e.getMessage());
        }

        return "redirect:/employer/applications";
    }
}
