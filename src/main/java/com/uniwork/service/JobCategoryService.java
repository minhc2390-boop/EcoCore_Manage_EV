package com.uniwork.service;

import com.uniwork.entity.JobCategory;

import java.util.List;

public interface JobCategoryService {
    List<JobCategory> getAllActiveCategories();
    JobCategory getCategoryById(Long id);
    JobCategory saveCategory(JobCategory category);
}
