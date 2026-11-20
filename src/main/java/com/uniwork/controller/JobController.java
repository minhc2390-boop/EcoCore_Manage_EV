package com.uniwork.controller;

import com.uniwork.dto.JobSearchDTO;
import com.uniwork.entity.JobPosting;
import com.uniwork.service.JobCategoryService;
import com.uniwork.service.JobPostingService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public String listJobs(@ModelAttribute JobSearchDTO searchDTO, Model model) {
        List<JobPosting> jobs = jobPostingService.searchJobs(searchDTO);
        model.addAttribute("jobs", jobs);
        model.addAttribute("categories", jobCategoryService.getAllActiveCategories());
        model.addAttribute("searchDTO", searchDTO);
        return "jobs/list";
    }

    @GetMapping("/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        jobPostingService.incrementViewCount(id);
        JobPosting job = jobPostingService.getJobById(id);
        model.addAttribute("job", job);
        return "jobs/detail";
    }
}
