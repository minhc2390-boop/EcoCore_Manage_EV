package com.uniwork.service.impl;

import com.uniwork.entity.JobCategory;
import com.uniwork.exception.ResourceNotFoundException;
import com.uniwork.repository.JobCategoryRepository;
import com.uniwork.service.JobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "activeJobCategories")
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
    @CacheEvict(value = "activeJobCategories", allEntries = true)
    public JobCategory saveCategory(JobCategory category) {
        return jobCategoryRepository.save(category);
    }
}
