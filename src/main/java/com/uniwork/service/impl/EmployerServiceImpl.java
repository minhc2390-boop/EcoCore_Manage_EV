package com.uniwork.service.impl;

import com.uniwork.entity.Employer;
import com.uniwork.entity.User;
import com.uniwork.exception.ResourceNotFoundException;
import com.uniwork.repository.EmployerRepository;
import com.uniwork.service.EmployerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Xử lý nghiệp vụ Doanh nghiệp / Nhà tuyển dụng
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Employer> findByUserId(Long userId) {
        return employerRepository.findByUserId(userId);
    }

    @Override
    public Employer getOrCreateEmployerForUser(User user) {
        return employerRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    log.info("Khởi tạo hồ sơ doanh nghiệp mới cho tài khoản Company Admin: {}", user.getUsername());
                    String companyName = (user.getFullName() != null && !user.getFullName().trim().isEmpty())
                            ? user.getFullName().trim()
                            : "Doanh nghiệp của " + user.getUsername();
                    Employer newEmployer = Employer.builder()
                            .companyName(companyName)
                            .address("TP. Cần Thơ")
                            .contactEmail(user.getEmail())
                            .contactPhone(user.getPhone())
                            .description("Đơn vị tuyển dụng sinh viên làm thêm và thực tập tại TP. Cần Thơ")
                            .status("APPROVED")
                            .user(user)
                            .build();
                    return employerRepository.save(newEmployer);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Employer getEmployerById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin doanh nghiệp mã: " + id));
    }

    @Override
    public Employer saveEmployer(Employer employer) {
        return employerRepository.save(employer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employer> getAllEmployers() {
        return employerRepository.findAllByOrderByCompanyNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employer> getEmployersByStatus(String status) {
        if (status == null || status.equalsIgnoreCase("ALL") || status.isBlank()) {
            return employerRepository.findAllByOrderByCompanyNameAsc();
        }
        return employerRepository.findByStatusOrderByCompanyNameAsc(status.toUpperCase());
    }

    @Override
    public Employer approveEmployer(Long id) {
        Employer employer = getEmployerById(id);
        employer.setStatus("APPROVED");
        log.info("Super Admin đã DUYỆT hồ sơ doanh nghiệp: {} (ID: {})", employer.getCompanyName(), id);
        return employerRepository.save(employer);
    }

    @Override
    public Employer lockEmployer(Long id) {
        Employer employer = getEmployerById(id);
        employer.setStatus("LOCKED");
        log.warn("Super Admin đã KHÓA doanh nghiệp vi phạm / nghi đa cấp: {} (ID: {})", employer.getCompanyName(), id);
        return employerRepository.save(employer);
    }

    @Override
    public Employer unlockEmployer(Long id) {
        Employer employer = getEmployerById(id);
        employer.setStatus("APPROVED");
        log.info("Super Admin đã MỞ KHÓA doanh nghiệp: {} (ID: {})", employer.getCompanyName(), id);
        return employerRepository.save(employer);
    }

    @Override
    @Transactional(readOnly = true)
    public long countEmployersByStatus(String status) {
        return employerRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalEmployers() {
        return employerRepository.count();
    }
}
