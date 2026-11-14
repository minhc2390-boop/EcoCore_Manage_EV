package com.studentjob.controller;

import com.studentjob.dto.ApiResponse;
import com.studentjob.dto.JobSearchDTO;
import com.studentjob.entity.JobPosting;
import com.studentjob.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller phục vụ tìm kiếm và truy vấn dữ liệu dạng JSON (Chuẩn Java 5 REST)
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobApiController {

    private final JobPostingService jobPostingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobPosting>>> getAllJobs() {
        List<JobPosting> jobs = jobPostingService.getAllActiveJobs();
        return ResponseEntity.ok(ApiResponse.ok(jobs, "Lấy danh sách việc làm thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobPosting>> getJobById(@PathVariable Long id) {
        JobPosting job = jobPostingService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.ok(job, "Lấy thông tin chi tiết việc làm thành công"));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<JobPosting>>> searchJobs(@RequestBody JobSearchDTO searchDTO) {
        List<JobPosting> jobs = jobPostingService.searchJobs(searchDTO);
        return ResponseEntity.ok(ApiResponse.ok(jobs, "Tìm kiếm việc làm thành công"));
    }
}
