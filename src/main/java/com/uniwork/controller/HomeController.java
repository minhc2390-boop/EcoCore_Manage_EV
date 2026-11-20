package com.uniwork.controller;

import com.uniwork.service.JobCategoryService;
import com.uniwork.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobPostingService jobPostingService;
    private final JobCategoryService jobCategoryService;

    @GetMapping({"/", "/home"})
    public String index(Model model) {
        model.addAttribute("latestJobs", jobPostingService.getLatestJobs());
        model.addAttribute("categories", jobCategoryService.getAllActiveCategories());
        return "index";
    }
}
