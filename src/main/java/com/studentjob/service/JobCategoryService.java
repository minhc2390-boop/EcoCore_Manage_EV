package com.studentjob.service;

import com.studentjob.entity.JobCategory;

import java.util.List;

public interface JobCategoryService {
    List<JobCategory> getAllActiveCategories();
    JobCategory getCategoryById(Long id);
    JobCategory saveCategory(JobCategory category);
}
