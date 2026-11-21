package com.uniwork.repository;

import com.uniwork.entity.CompanyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyReviewRepository extends JpaRepository<CompanyReview, Long> {
    List<CompanyReview> findByEmployerIdOrderByCreatedAtDesc(Long employerId);
    long countByEmployerId(Long employerId);
}
