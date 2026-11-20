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
}
