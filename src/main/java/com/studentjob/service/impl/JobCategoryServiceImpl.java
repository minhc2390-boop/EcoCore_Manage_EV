package com.studentjob.service.impl;

import com.studentjob.entity.JobCategory;
import com.studentjob.exception.ResourceNotFoundException;
import com.studentjob.repository.JobCategoryRepository;
import com.studentjob.service.JobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobCategoryServiceImpl implements JobCategoryService {

    private final JobCategoryRepository jobCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobCategory> getAllActiveCategories() {
        return jobCategoryRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public JobCategory getCategoryById(Long id) {
        return jobCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục ngành nghề: " + id));
    }

    @Override
    public JobCategory saveCategory(JobCategory category) {
        return jobCategoryRepository.save(category);
    }
}
