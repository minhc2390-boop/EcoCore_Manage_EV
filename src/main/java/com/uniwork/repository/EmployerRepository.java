package com.uniwork.repository;

import com.uniwork.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Quản lý Nhà tuyển dụng / Doanh nghiệp
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByUserId(Long userId);
    Optional<Employer> findByCompanyNameIgnoreCase(String companyName);
    List<Employer> findByStatusOrderByCompanyNameAsc(String status);
    List<Employer> findAllByOrderByCompanyNameAsc();
    long countByStatus(String status);
}
